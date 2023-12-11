package hu.krtn.brigad.test;

import hu.krtn.brigad.engine.rendering.shading.FragmentShaderData;
import hu.krtn.brigad.engine.rendering.shading.KrtnShaderInterpreter;
import hu.krtn.brigad.engine.rendering.shading.VertexShaderData;
import hu.krtn.brigad.engine.resources.ResourceManager;

public class Test {

    public static void main(String[] args) throws Exception {
        //ScriptEngine engine = new NashornScriptEngineFactory().getScriptEngine();
        //System.out.println(engine.eval("1 + 2"));
//
        //// add java method to javascript
        //engine.put("x", 10);
        //engine.eval("function hello(name) { print('Hello, ' + name + ' ' + x); }");
        //engine.eval("hello('World');");
//
        //// add test method to javascript
        //engine.put("test", (Runnable) Test::test);
        //engine.eval("test();");

        String path = "./resources/shaders/fragment/pbr_v2.glsl";
        FragmentShaderData interpret = KrtnShaderInterpreter.interpretFragmentShader(path, ResourceManager.getInstance().readTextFile(path));
        path = "./resources/shaders/vertex/pbr.glsl";
        VertexShaderData interpretV = KrtnShaderInterpreter.interpretVertexShader(path, ResourceManager.getInstance().readTextFile(path));

        System.out.println("Done!");
    }

    private static void test() {
        System.out.println("Hello World!");
    }

}
