#   JAVASSIST
http://www.javassist.org/tutorial/tutorial.html

Java 字节码以二进制的形式存储在 .class 文件中，每一个 .class 文件包含一个 Java 类或接口。Javaassist 就是一个用来 处理 Java 字节码的类库。它可以在一个已经编译好的类中添加新的方法，或者是修改已有的方法，并且不需要对字节码方面有深入的了解。同时也可以去生成一个新的类对象，通过完全手动的方式。

在 Javassist 中，类 Javaassit.CtClass 表示 class 文件。一个 GtClass (编译时类）对象可以处理一个 class 文件，ClassPool是 CtClass 对象的容器。它按需读取类文件来构造 CtClass 对象，并且保存 CtClass 对象以便以后使用。

需要注意的是 ClassPool 会在内存中维护所有被它创建过的 CtClass，当 CtClass 数量过多时，会占用大量的内存，API中给出的解决方案是 有意识的调用CtClass的detach()方法以释放内存。

##  ClassPool需要关注的方法：
    getDefault : 返回默认的ClassPool 是单例模式的，一般通过该方法创建我们的ClassPool；
    appendClassPath, insertClassPath : 将一个ClassPath加到类搜索路径的末尾位置 或 插入到起始位置。通常通过该方法写入额外的类搜索路径，以解决多个类加载器环境中找不到类的尴尬；
    toClass : 将修改后的CtClass加载至当前线程的上下文类加载器中，CtClass的toClass方法是通过调用本方法实现。需要注意的是一旦调用该方法，则无法继续修改已经被加载的class；
    get , getCtClass : 根据类路径名获取该类的CtClass对象，用于后续的编辑。

##  CtClass需要关注的方法：

    freeze : 冻结一个类，使其不可修改；
    isFrozen : 判断一个类是否已被冻结；
    prune : 删除类不必要的属性，以减少内存占用。调用该方法后，许多方法无法将无法正常使用，慎用；
    defrost : 解冻一个类，使其可以被修改。如果事先知道一个类会被defrost， 则禁止调用 prune 方法；
    detach : 将该class从ClassPool中删除；
    writeFile : 根据CtClass生成 .class 文件；
    toClass : 通过类加载器加载该CtClass。


## CtMethod中的一些重要方法：
创建一个新的方法使用了CtMethod类。CtMthod代表类中的某个方法，可以通过CtClass提供的API获取或者CtNewMethod新建，通过CtMethod对象可以实现对方法的修改。

    insertBefore : 在方法的起始位置插入代码；
    insterAfter : 在方法的所有 return 语句前插入代码以确保语句能够被执行，除非遇到exception；
    insertAt : 在指定的位置插入代码；
    setBody : 将方法的内容设置为要写入的代码，当方法被 abstract修饰时，该修饰符被移除；
    make : 创建一个新的方法。
        setBody()的时候我们使用了一些符号：
        // $0=this / $1,$2,$3... 代表方法参数
        cons.setBody("{$0.name = $1;}");

#   2. 调用生成的类对象#
##  1. 通过反射的方式调用
    上面的案例是创建一个类对象然后输出该对象编译完之后的 .class 文件。那如果我们想调用生成的类对象中的属性或者方法应该怎么去做呢？javassist也提供了相应的api，生成类对象的代码还是和第一段一样，将最后写入文件的代码替换为如下：
    
    Copy
    // 这里不写入文件，直接实例化
    Object person = cc.toClass().newInstance();
    // 设置值
    Method setName = person.getClass().getMethod("setName", String.class);
    setName.invoke(person, "cunhua");
    // 输出值
    Method execute = person.getClass().getMethod("printName");
    execute.invoke(person);
    然后执行main方法就可以看到调用了 printName方法。

##  2. 通过读取 .class 文件的方式调用
    Copy
    ClassPool pool = ClassPool.getDefault();
    // 设置类路径
    pool.appendClassPath("/Users/yangyue/workspace/springboot-learn/java-agent/src/main/java/");
    CtClass ctClass = pool.get("com.rickiyang.learn.javassist.Person");
    Object person = ctClass.toClass().newInstance();
    //  ...... 下面和通过反射的方式一样去使用
##  3. 通过接口的方式
    上面两种其实都是通过反射的方式去调用，问题在于我们的工程中其实并没有这个类对象，所以反射的方式比较麻烦，并且开销也很大。那么如果你的类对象可以抽象为一些方法得合集，就可以考虑为该类生成一个接口类。这样在newInstance()的时候我们就可以强转为接口，可以将反射的那一套省略掉了。
    
    还拿上面的Person类来说，新建一个PersonI接口类：
    
    Copy
    package com.rickiyang.learn.javassist;
    
    /**
     * @author rickiyang
     * @date 2019-08-07
     * @Desc
     */
    public interface PersonI {
    
        void setName(String name);
    
        String getName();
    
        void printName();
    
    }
    实现部分的代码如下：
    
    Copy
    ClassPool pool = ClassPool.getDefault();
    pool.appendClassPath("/Users/yangyue/workspace/springboot-learn/java-agent/src/main/java/");
    
    // 获取接口
    CtClass codeClassI = pool.get("com.rickiyang.learn.javassist.PersonI");
    // 获取上面生成的类
    CtClass ctClass = pool.get("com.rickiyang.learn.javassist.Person");
    // 使代码生成的类，实现 PersonI 接口
    ctClass.setInterfaces(new CtClass[]{codeClassI});
    
    // 以下通过接口直接调用 强转
    PersonI person = (PersonI)ctClass.toClass().newInstance();
    System.out.println(person.getName());
    person.setName("xiaolv");
    person.printName();
    使用起来很轻松。

#   2. 修改现有的类对象#
    前面说到新增一个类对象。这个使用场景目前还没有遇到过，一般会遇到的使用场景应该是修改已有的类。比如常见的日志切面，权限切面。我们利用javassist来实现这个功能。
    
    有如下类对象：
    
    Copy
    package com.rickiyang.learn.javassist;
    
    /**
     * @author rickiyang
     * @date 2019-08-07
     * @Desc
     */
    public class PersonService {
    
        public void getPerson(){
            System.out.println("get Person");
        }
    
        public void personFly(){
            System.out.println("oh my god,I can fly");
        }
    }
    
    然后对他进行修改：
    
    Copy
    package com.rickiyang.learn.javassist;
    
    import javassist.ClassPool;
    import javassist.CtClass;
    import javassist.CtMethod;
    import javassist.Modifier;
    
    import java.lang.reflect.Method;
    
    /**
     * @author rickiyang
     * @date 2019-08-07
     * @Desc
     */
    public class UpdatePerson {
    
        public static void update() throws Exception {
            ClassPool pool = ClassPool.getDefault();
            CtClass cc = pool.get("com.rickiyang.learn.javassist.PersonService");
    
            CtMethod personFly = cc.getDeclaredMethod("personFly");
            personFly.insertBefore("System.out.println(\"起飞之前准备降落伞\");");
            personFly.insertAfter("System.out.println(\"成功落地。。。。\");");
    
    
            //新增一个方法
            CtMethod ctMethod = new CtMethod(CtClass.voidType, "joinFriend", new CtClass[]{}, cc);
            ctMethod.setModifiers(Modifier.PUBLIC);
            ctMethod.setBody("{System.out.println(\"i want to be your friend\");}");
            cc.addMethod(ctMethod);
    
            Object person = cc.toClass().newInstance();
            // 调用 personFly 方法
            Method personFlyMethod = person.getClass().getMethod("personFly");
            personFlyMethod.invoke(person);
            //调用 joinFriend 方法
            Method execute = person.getClass().getMethod("joinFriend");
            execute.invoke(person);
        }
    
        public static void main(String[] args) {
            try {
                update();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    在personFly方法前后加上了打印日志。然后新增了一个方法joinFriend。执行main函数可以发现已经添加上了。
    
    另外需要注意的是：上面的insertBefore() 和 setBody()中的语句，如果你是单行语句可以直接用双引号，但是有多行语句的情况下，你需要将多行语句用{}括起来。javassist只接受单个语句或用大括号括起来的语句块。
    
#   概述

Javassist是一个开源的分析、编辑和创建Java字节码的类库，可以直接编辑和生成Java生成的字节码。相对于bcel, asm等这些工具，开发者不需要了解虚拟机指令，就能动态改变类的结构，或者动态生成类。javassist简单易用， 快速。    
    
    重要的类

    ClassPool：javassist的类池，使用ClassPool 类可以跟踪和控制所操作的类，它的工作方式与 JVM 类装载器非常相似
    CtClass： CtClass提供了类的操作，如在类中动态添加新字段、方法和构造函数、以及改变类、父类和接口的方法。
    CtField：类的属性，通过它可以给类创建新的属性，还可以修改已有的属性的类型，访问修饰符等
    CtMethod：类中的方法，通过它可以给类创建新的方法，还可以修改返回类型，访问修饰符等， 甚至还可以修改方法体内容代码
    CtConstructor：与CtMethod类似

#   API运用

    ClassPool
    
    // 类库, jvm中所加载的class
    ClassPool pool = ClassPool.getDefault();
    // 加载一个已知的类, 注：参数必须为全量类名
    CtClass ctClass = pool.get("com.itheima.Student");
    // 创建一个新的类, 类名必须为全量类名
    CtClass tClass = pool.makeClass("com.itheima.Calculator");
    CtField
    
    // 获取已知类的属性
    CtField ctField = ctClass.getDeclaredField("name");
    // 构建新的类的成员变量
    CtField ctFieldNew = new CtField(CtClass.intType,"age",ctClass);
    // 设置类的访问修饰符为public
    ctFieldNew.setModifiers(Modifier.PUBLIC);
    // 将属性添加到类中
    ctClass.addField(ctFieldNew);
    CtMethod
    
    1 // 获取已有方法
    
    2 CtMethod ctMethod = ctClass.getDeclaredMethod("sayHello");
    
    3 //创建新的方法, 参数1:方法的返回类型，参数2：名称，参数3：方法的参数，参数4：方法所属的类
    
    5 CtMethod ctMethod = new CtMethod(CtClass.intType, "calc", new CtClass[]
    
    {CtClass.intType,CtClass.intType}, tClass);
    
    6 // 设置方法的访问修饰
    
    7 ctMethod.setModifiers(Modifier.PUBLIC);
    
    8 // 将新建的方法添加到类中
    
    9 ctClass.addMethod(ctMethod);
    
    10 // 方法体内容代码 $1代表第一个参数，$2代表第二个参数
    
    ctMethod.setBody("return $1 + $2;");
    
    11// 直接创建方法
    
    CtMethod getMethod = CtNewMethod.make("public int getAge() { return this.age;}", ctClass); CtMethod setMethod = CtNewMethod.make("public void setAge(int age) { this.age = age;}", ctClass);
    
    ctClass.addMethod(getMethod);
    
    ctClass.addMethod(setMethod);
    
    CtConstructor
    
    
    案例
    
    创建maven工程并添加依赖
    
    
    添加依赖
    
    添加Student类
    
    package com.itheima;
    
    public class Student {
    
    private int age; private String name;
    
    public int getAge() { return age;
    
    }
    
    public void setAge(int age) { this.age = age;
    
    }
    
    public String getName() { return name;
    
    }
    
    public void setName(String name) { this.name = name;
    
    }
    
    public void sayHello(){
    
    System.out.println("Hello, My name is " + this.name);
    
    }
    
    }
    
    修改已有方法体，插入新的代码
    
    对已有的student类中的sayHello方法，当调用时，控制台会输出: Hello, My name is 张三(name=张三)
    
    需求：通过动态修改sayHello方法，当调用sayHello时，除了输出已经的内容外，再输出当前学生的age信息 创建JavassistDemo测试类，代码实现如下：
![](https://pics5.baidu.com/feed/3ac79f3df8dcd1003abd3a5e84621116b8122f98.png?token=ec15b57df0aa48f35f8778942a6a42cf&s=7594EC3B91AFD14D5EF5A4D8030080B3)    
    
    运行结果:
    
    
    动态添加方法
    
    接下来我们给Student类添加一个计算的方法，但不是直接在Student类中添加，而是使用javassist，动态添加
    
    public int calculate(int a, int b){
    return a+b;
    创建测试方法t2，代码如下
    
    @Test
    public void t2() throws Exception{
    // 类库池, jvm中所加载的class
    ClassPool pool = ClassPool.getDefault();
    // 获取指定的Student类
    CtClass ctClass = pool.get("com.itheima.Student");
    // 创建calc方法, 带两个参数，参数的类型都为int类型
    CtMethod ctMethod = new CtMethod(CtClass.intType,"calc",
    new CtClass[]{CtClass.intType,CtClass.intType}, ctClass);
    // 设置方法的访问修饰
    ctMethod.setModifiers(Modifier.PUBLIC);
    // 设置方法体代码
    ctMethod.setBody("return $1 + $2;");
    // 添加新建的方法到原有的类中
    ctClass.addMethod(ctMethod);
    // 加载修改后的类
    ctClass.toClass();
    // 创建对象
    Student stu = new Student();
    // 获取calc方法
    Method dMethod = Student.class.getDeclaredMethod("calc", new Class[]{int.class,int.class});
    // 反射调用 方法
    Object result = dMethod.invoke(stu, 10,20);
    // 打印结果
    System.out.println(String.format("调用calc方法，传入参数：%d,%d", 10,20));
    System.out.println("返回结果：" + (int)result);
    控制台输出结果：
    
    
    动态创建类
    
    下面我们再来个神的魔术，无中生有
    
    @Test
    public void t3() throws Exception{
    ClassPool pool = ClassPool.getDefault();
    // 创建teacher类
    CtClass teacherClass = pool.makeClass("com.itheima.Teacher");
    // 设置为公有类
    teacherClass.setModifiers(Modifier.PUBLIC);
    // 获取String类型
    CtClass stringClass = pool.get("java.lang.String");
    // 获取list类型
    CtClass listClass = pool.get("java.util.List");
    // 获取学生的类型
    CtClass studentClass = pool.get("com.itheima.Student");
    // 给teacher添加name属性
    CtField nameField = new CtField(stringClass, "name", teacherClass);
    nameField.setModifiers(Modifier.PUBLIC);
    teacherClass.addField(nameField);
    // 给teacher类添加students属性
    CtField studentList = new CtField(listClass, "students",teacherClass);
    studentList.setModifiers(Modifier.PUBLIC);
    teacherClass.addField(studentList); 22
    // 给teacher类添加无参构造方法
    CtConstructor ctConstructor = CtNewConstructor.make("public Teacher(){this.name=\"abc\";this.students = new java.util.ArrayList();}", teacherClass);
    teacherClass.addConstructor(ctConstructor); 26
    // 给teacher类添加addStudent方法
    CtMethod m = new CtMethod(CtClass.voidType, "addStudent", new CtClass[]{studentClass}, teacherClass);
    m.setModifiers(Modifier.PUBLIC);
    // 添加学生对象到students属性中, $1代表参数1
    m.setBody("this.students.add($1);");
    teacherClass.addMethod(m); 33
    // 给teacher类添加sayHello方法
    m = new CtMethod(CtClass.voidType, "sayHello", new CtClass[]{}, teacherClass);
    m.setModifiers(Modifier.PUBLIC);
    m.setBody("System.out.println(\"Hello, My name is \" + this.name);");
    m.insertAfter("System.out.println(\"I have \" + this.students.size() + \" students\");");
    teacherClass.addMethod(m); 40
    // 加载修改后的类
    Class<?> cls = teacherClass.toClass();
    // 实例teacher对象
    Object obj = cls.newInstance();
    // 获取addStudent方法
    Method method = cls.getDeclaredMethod("addStudent", Student.class);
    // 创建张三和李四2个学生
    Student stu = new Student();
    stu.setName("张三");
    // 调用teacher类的addStudent方法，添加张三
    method.invoke(obj, stu);
    stu = new Student();
    stu.setName("李四");
    // 调用teacher类的addStudent方法，添加李四
    method.invoke(obj, stu);
    // 调用sayHello方法
    Method teacherSayHello = cls.getDeclaredMethod("sayHello",new Class[]{});
    teacherSayHello.invoke(obj, new Object[]{});
    }
    
    运行结果：
    
    
    总结
    
    javassist被用于struts2和hibernate中，都用来做动态字节码修改使用。一般开发中不会用到，但在封装框架时比 较有用。虽然javassist提供了一套简单易用的API，但如果用于平常的开发，会有如下几点不好的地方:
    
    所引用的类型，必须通过ClassPool获取后才可以使用
    代码块中所用到的引用类型，使用时必须写全量类名
    即使代码块内容写错了，它也不会像eclipse等开发工具一样有提示，它只有在运行时才报错
    动态修改的类，必须在修改之前，jvm中不存在这个类的实例对象。修改方法的实现必须在修改的类加载之前 进行