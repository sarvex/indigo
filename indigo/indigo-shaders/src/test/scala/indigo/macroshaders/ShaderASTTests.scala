package indigo.macroshaders

import ShaderDSL.*

class ShaderASTTests extends munit.FunSuite {

  trait FragEnv {
    val UV: vec2
  }

  test("Build an Indigo compatible fragment shader AST") {
    import ShaderAST.*
    import ShaderAST.DataTypes.*

    val expected: ProceduralShader =
      ProceduralShader(
        List(
          Function(
            "fn0",
            List("env"),
            Block(
              List(
                NamedBlock("", "Program", List(vec4(List(float(1), float(1), float(0), float(1)))))
              )
            ),
            None
          )
        ),
        NamedBlock(
          "",
          "Shader",
          List(
            Block(List(Block(List(CallFunction("fn0", Nil)))))
          )
        )
      )

    inline def fragment: Shader[FragEnv, rgba] =
      Shader { env =>
        Program(rgba(1.0f, 1.0f, 0.0f, 1.0f))
      }

    val actual =
      ShaderMacros.toAST(fragment)

    // println(">>>>>>")
    // println(actual)
    // println("----")
    // println(fragment.toGLSL)
    // println("<<<<<<")

    assertEquals(actual, expected)
  }

  test("Inlined external val") {

    inline def alpha: Float = 1.0f

    inline def fragment: Shader[FragEnv, rgba] =
      Shader { env =>
        Program(rgba(1.0f, 1.0f, 0.0f, alpha))
      }

    val actual =
      ShaderMacros.toAST(fragment)

    // println(">>>>>>")
    // println(actual)
    // println("----")
    // println(fragment.toGLSL)
    // println("<<<<<<")

    val p: Boolean = {
      import ShaderAST.*
      import ShaderAST.DataTypes.*

      actual.exists(_ == vec4(List(float(1), float(1), float(0), float(1))))
    }

    assert(clue(p))
  }

  test("Inlined external val (as def)") {

    inline def zw: vec2 = vec2(0.0f, 1.0f)

    inline def fragment: Shader[FragEnv, rgba] =
      Shader { env =>
        Program(rgba(1.0f, 1.0f, zw))
      }

    val actual =
      ShaderMacros.toAST(fragment)

    // println(">>>>>>")
    // println(actual)
    // println("----")
    // println(fragment.toGLSL)
    // println("<<<<<<")

    val p: Boolean = {
      import ShaderAST.*
      import ShaderAST.DataTypes.*

      actual.exists(_ == vec4(List(float(1), float(1), vec2(0.0f, 1.0f))))
    }

    assert(clue(p))
  }

  test("Inlined external function") {

    inline def xy(v: Float): vec2 =
      vec2(v)

    inline def zw: Float => vec2 =
      alpha => vec2(0.0f, alpha)

    inline def fragment: Shader[FragEnv, rgba] =
      Shader { env =>
        Program(rgba(xy(1.0f), zw(1.0f)))
      }

    val actual =
      ShaderMacros.toAST(fragment)

    // println(">>>>>>")
    // println(actual)
    // println("----")
    // println(fragment.toGLSL)
    // println("<<<<<<")

    val p: Boolean = {
      import ShaderAST.*
      import ShaderAST.DataTypes.*

      val expected =
        Function("fn1", List("float alpha"), Block(List(vec2(List(float(0), ident("alpha"))))), Some("vec2"))

      actual.exists(_ == expected)
    }

    assert(clue(p))
  }

  test("Inlined external function N args") {

    // Is inlined, which might not be what we want?
    inline def xy(red: Float, green: Float): vec2 =
      vec2(red, green)

    // Is treated like a function
    inline def zw: (Float, Float) => vec2 =
      (blue, alpha) => vec2(blue, alpha)

    inline def fragment: Shader[FragEnv, rgba] =
      Shader { env =>
        Program(rgba(xy(1.0f, 0.25f), zw(0.5f, 1.0f)))
      }

    val actual =
      ShaderMacros.toAST(fragment)

    // println(">>>>>>")
    // println(actual)
    // println("----")
    // println(fragment.toGLSL)
    // println("<<<<<<")

    val p: Boolean = {
      import ShaderAST.*
      import ShaderAST.DataTypes.*

      val expected1 =
        Function(
          "fn1",
          List("float blue", "float alpha"),
          Block(List(vec2(List(ident("blue"), ident("alpha"))))),
          Some("vec2")
        )

      val expected2 =
        vec2(1.0f, 0.25f)

      actual.exists(_ == expected1) && actual.exists(_ == expected2)
    }

    assert(p)
  }

  // test("flatMapped Program") {

  //   inline def zw: vec2 = vec2(0.0f, 1.0f)

  //   inline def fragment: Shader[FragEnv, rgba] =
  //     Shader { env =>
  //       Program(rgba(1.0f, 1.0f, zw))
  //     }

  //   val actual =
  //     ShaderMacros.toAST(fragment)

  //   // println(">>>>>>")
  //   // println(actual)
  //   // println("----")
  //   // println(fragment.toGLSL)
  //   // println("<<<<<<")

  //   val p: Boolean = {
  //     import ShaderAST.*
  //     import ShaderAST.DataTypes.*

  //     actual.exists(_ == vec4(List(float(1), float(1), vec2(0.0f, 1.0f))))
  //   }

  //   assert(clue(p))
  // }

  // test("Programs can use an env value like env.UV") {

  //   inline def zw: vec2 = vec2(0.0f, 1.0f)

  //   inline def fragment: Shader[FragEnv, rgba] =
  //     Shader { env =>
  //       Program(rgba(1.0f, 1.0f, zw))
  //     }

  //   val actual =
  //     ShaderMacros.toAST(fragment)

  //   // println(">>>>>>")
  //   // println(actual)
  //   // println("----")
  //   // println(fragment.toGLSL)
  //   // println("<<<<<<")

  //   val p: Boolean = {
  //     import ShaderAST.*
  //     import ShaderAST.DataTypes.*

  //     actual.exists(_ == vec4(List(float(1), float(1), vec2(0.0f, 1.0f))))
  //   }

  //   assert(clue(p))
  // }

}
