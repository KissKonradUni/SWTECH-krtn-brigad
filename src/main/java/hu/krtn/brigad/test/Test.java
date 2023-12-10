package hu.krtn.brigad.test;

import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptEngine;

public class Test {

    public static void main(String[] args) throws Exception {
        ScriptEngine engine = new NashornScriptEngineFactory().getScriptEngine();
        System.out.println(engine.eval("1 + 2"));

        // add java method to javascript
        engine.put("x", 10);
        engine.eval("function hello(name) { print('Hello, ' + name + ' ' + x); }");
        engine.eval("hello('World');");

        // add test method to javascript
        engine.put("test", (Runnable) Test::test);
        engine.eval("test();");
    }

    private static void test() {
        System.out.println("Hello World!");
    }

}
