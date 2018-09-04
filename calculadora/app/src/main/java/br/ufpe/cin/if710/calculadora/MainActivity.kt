package br.ufpe.cin.if710.calculadora

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : Activity() {
    var result = ""
    var express = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //aqui eu capturo todos os elementos da view para poder criar os listener
        val button0 = findViewById<Button>(R.id.btn_0)
        val button1 = findViewById<Button>(R.id.btn_1)
        val button2 = findViewById<Button>(R.id.btn_2)
        val button3 = findViewById<Button>(R.id.btn_3)
        val button4 = findViewById<Button>(R.id.btn_4)
        val button5 = findViewById<Button>(R.id.btn_5)
        val button6 = findViewById<Button>(R.id.btn_6)
        val button7 = findViewById<Button>(R.id.btn_7)
        val button8 = findViewById<Button>(R.id.btn_8)
        val button9 = findViewById<Button>(R.id.btn_9)
        val buttonEqual = findViewById<Button>(R.id.btn_Equal)
        val buttonDot = findViewById<Button>(R.id.btn_Dot)
        val buttonDivide = findViewById<Button>(R.id.btn_Divide)
        val buttonMultiply = findViewById<Button>(R.id.btn_Multiply)
        val buttonSubtract = findViewById<Button>(R.id.btn_Subtract)
        val buttonAdd = findViewById<Button>(R.id.btn_Add)
        val buttonClear = findViewById<Button>(R.id.btn_Clear)
        val buttonPower = findViewById<Button>(R.id.btn_Power)
        val buttonRParen = findViewById<Button>(R.id.btn_RParen)
        val buttonLParen = findViewById<Button>(R.id.btn_LParen)
        val textCalc = findViewById<TextView>(R.id.text_calc)
        val textInfo = findViewById<TextView>(R.id.text_info)

        //Essa funcao serve para montarmos a expressao no text_calc
        fun setExpress(aux: String){
            express = express + aux
            textCalc.text = express
        }
        //Essa funcao serve para limpar caracter por caracter da expressao no text_calc
        fun clearExpress(){
            if(express!=""){
                express = express.removeRange(express.length-1,express.length)
            }else{
                result = ""
                text_info.text = result
            }
            textCalc.text = express
        }
        //Essa funcao computa o resultado da expressao e trata as exceptions exibindo mensagens em um toast
        fun result(aux: String){
            try{
                val aux = eval(aux)
                textCalc.text = aux.toString()
                textInfo.text = aux.toString()
                express = aux.toString()
            }catch (e : RuntimeException){
                Toast.makeText(this,e.message,Toast.LENGTH_LONG).show()
            }
        }
        //Aqui eu crio as escutas dos elementos ligando com as chamadas de metodos
        button0.setOnClickListener { setExpress("0")}
        button1.setOnClickListener { setExpress("1")}
        button2.setOnClickListener { setExpress("2")}
        button3.setOnClickListener { setExpress("3")}
        button4.setOnClickListener { setExpress("4")}
        button5.setOnClickListener { setExpress("5")}
        button6.setOnClickListener { setExpress("6")}
        button7.setOnClickListener { setExpress("7")}
        button8.setOnClickListener { setExpress("8")}
        button9.setOnClickListener { setExpress("9")}
        buttonDot.setOnClickListener { setExpress(".")}
        buttonDivide.setOnClickListener { setExpress("/")}
        buttonMultiply.setOnClickListener { setExpress("*")}
        buttonSubtract.setOnClickListener { setExpress("-")}
        buttonAdd.setOnClickListener { setExpress("+")}
        buttonPower.setOnClickListener { setExpress("^")}
        buttonLParen.setOnClickListener { setExpress("(")}
        buttonRParen.setOnClickListener { setExpress(")")}
        buttonClear.setOnClickListener { clearExpress()}
        buttonEqual.setOnClickListener{result(express)}

    }

    //Aqui eu salvo o estado da view para quando for necessario restaurar o estado posteriormente
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (outState != null) {
            outState.putString("calc", text_calc.text.toString())
            outState.putString("info", text_info.text.toString())
            Log.d("info",text_info.text.toString())
            Log.d("calc",text_calc.text.toString())
        }
    }

    //Aqui eu restauro as informacoes da view do estado anterior
    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if(savedInstanceState!=null){
            val t_info:String=savedInstanceState.getString("info")
            val t_calc:String=savedInstanceState.getString("calc")
            text_info.setText(t_info)
            result = t_info
            text_calc.setText(t_calc)
            express = t_calc
            Log.d("onRestore",t_info)
            Log.d("onRestore",t_calc)
        }

    }
    //Como usar a função:
    // eval("2+2") == 4.0
    // eval("2+3*4") = 14.0
    // eval("(2+3)*4") = 20.0
    //Fonte: https://stackoverflow.com/a/26227947
    fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch: Char = ' '
            fun nextChar() {
                val size = str.length
                ch = if ((++pos < size)) str.get(pos) else (-1).toChar()
            }

            fun eat(charToEat: Char): Boolean {
                while (ch == ' ') nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Caractere inesperado: " + ch)
                return x
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            // | number | functionName factor | factor `^` factor
            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'))
                        x += parseTerm() // adição
                    else if (eat('-'))
                        x -= parseTerm() // subtração
                    else
                        return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'))
                        x *= parseFactor() // multiplicação
                    else if (eat('/'))
                        x /= parseFactor() // divisão
                    else
                        return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+')) return parseFactor() // + unário
                if (eat('-')) return -parseFactor() // - unário
                var x: Double
                val startPos = this.pos
                if (eat('(')) { // parênteses
                    x = parseExpression()
                    eat(')')
                } else if ((ch in '0'..'9') || ch == '.') { // números
                    while ((ch in '0'..'9') || ch == '.') nextChar()
                    x = java.lang.Double.parseDouble(str.substring(startPos, this.pos))
                } else if (ch in 'a'..'z') { // funções
                    while (ch in 'a'..'z') nextChar()
                    val func = str.substring(startPos, this.pos)
                    x = parseFactor()
                    if (func == "sqrt")
                        x = Math.sqrt(x)
                    else if (func == "sin")
                        x = Math.sin(Math.toRadians(x))
                    else if (func == "cos")
                        x = Math.cos(Math.toRadians(x))
                    else if (func == "tan")
                        x = Math.tan(Math.toRadians(x))
                    else
                        throw RuntimeException("Função desconhecida: " + func)
                } else {
                    throw RuntimeException("Caractere inesperado: " + ch.toChar())
                }
                if (eat('^')) x = Math.pow(x, parseFactor()) // potência
                return x
            }
        }.parse()
    }
}
