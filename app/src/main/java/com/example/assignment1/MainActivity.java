package com.example.assignment1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView values, answer;
    String input = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        values = findViewById(R.id.values);
        answer = findViewById(R.id.Answer);
        Button btn0 = findViewById(R.id.btn_zero);
        Button btn1 = findViewById(R.id.btn_one);
        Button btn2 = findViewById(R.id.btn_two);
        Button btn3 = findViewById(R.id.btn_three);
        Button btn4 = findViewById(R.id.btn_four);
        Button btn5 = findViewById(R.id.btn_five);
        Button btn6 = findViewById(R.id.btn_six);
        Button btn7 = findViewById(R.id.btn_seven);
        Button btn8 = findViewById(R.id.btn_eight);
        Button btn9 = findViewById(R.id.btn_nine);
        Button btnDivide = findViewById(R.id.btn_divide);
        Button btnMultiply = findViewById(R.id.btn_multiply);
        Button btnMinus = findViewById(R.id.btn_minus);
        Button btnPlus = findViewById(R.id.btn_plus);
        Button btnDot = findViewById(R.id.btn_dot);
        Button btnEquals = findViewById(R.id.btn_equal);
        Button btnAC = findViewById(R.id.btn_ac);
        Button btnModulus = findViewById(R.id.btn_mod);
        Button btnSin = findViewById(R.id.btn_sin);
        Button btnCos = findViewById(R.id.btn_cos);
        Button btnTan = findViewById(R.id.btn_tan);
        Button btnLog = findViewById(R.id.btn_log);
        Button btnLn = findViewById(R.id.btn_ln);
        Button btnSqrt = findViewById(R.id.btn_sqrt);
        Button btnFactorial = findViewById(R.id.btn_fact);
        Button btnPi = findViewById(R.id.btn_pi);
        Button btnSquare = findViewById(R.id.btn_square);
        Button btnCube = findViewById(R.id.btn_cube);
        View.OnClickListener listener = v -> {
            Button b = (Button) v;
            input += b.getText().toString();
            values.setText(input);
        };

        Button[] buttons = {
                btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9,
                btnDivide, btnMultiply, btnMinus, btnPlus, btnDot, btnModulus
        };
        for (Button b : buttons) b.setOnClickListener(listener);


        btnAC.setOnClickListener(v -> {
            input = "";
            values.setText("");
            answer.setText("0");
        });


        btnSin.setOnClickListener(v -> { input += "sin("; values.setText(input); });
        btnCos.setOnClickListener(v -> { input += "cos("; values.setText(input); });
        btnTan.setOnClickListener(v -> { input += "tan("; values.setText(input); });
        btnLog.setOnClickListener(v -> { input += "log("; values.setText(input); });
        btnLn.setOnClickListener(v -> { input += "ln("; values.setText(input); });
        btnSqrt.setOnClickListener(v -> { input += "sqrt("; values.setText(input); });
        btnFactorial.setOnClickListener(v -> { input += "!"; values.setText(input); });

        btnPi.setOnClickListener(v -> { input += "pi"; values.setText(input); });
        btnSquare.setOnClickListener(v -> { input += "^2"; values.setText(input); });
        btnCube.setOnClickListener(v -> { input += "^3"; values.setText(input); });

        // Equals
        btnEquals.setOnClickListener(v -> {
            if (input.isEmpty()) return;
            try {
                String expr = input.replace('×', '*').replace('÷', '/');
                expr = expr.replaceAll("(\\d+(?:\\.\\d+)?)%(?!\\d)", "($1/100)");
                expr = expr.replaceAll("(?i)\\bSIN\\b", "sin")
                        .replaceAll("(?i)\\bCOS\\b", "cos")
                        .replaceAll("(?i)\\bTAN\\b", "tan")
                        .replaceAll("(?i)\\bLOG\\b", "log")
                        .replaceAll("(?i)\\bLN\\b", "ln")
                        .replaceAll("(?i)\\bSQRT\\b", "sqrt")
                        .replaceAll("(?i)\\bPI\\b", String.valueOf(Math.PI));
                expr = expr.replaceAll("(?<=[0-9\\)])(?=[A-Za-z(])", "*");
                int open = 0;
                for (int i = 0; i < expr.length(); i++) {
                    char c = expr.charAt(i);
                    if (c == '(') open++;
                    else if (c == ')') open = Math.max(0, open - 1);
                }
                if (open > 0) {
                    StringBuilder sb = new StringBuilder(expr);
                    for (int i = 0; i < open; i++) sb.append(')');
                    expr = sb.toString();
                }

                double result = evaluate(expr);

                if (Double.isNaN(result) || Double.isInfinite(result)) {
                    answer.setText("Error");
                } else {
                    if (Math.floor(result) == result)
                        answer.setText(String.valueOf((long) result));
                    else
                        answer.setText(String.valueOf(result));
                }
            } catch (Exception e) {
                answer.setText("Error");
                e.printStackTrace();
            }
        });
    }
    private double evaluate(final String str) {
        return new Object() {
            int pos = -1, ch;
            void nextChar() { ch = (++pos < str.length()) ? str.charAt(pos) : -1; }
            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) { nextChar(); return true; }
                return false;
            }
            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }
            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }
            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else if (eat('%')) x %= parseFactor();
                    else return x;
                }
            }
            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();
                double x;
                int startPos = this.pos;

                if (eat('(')) {
                    x = parseExpression();
                    if (!eat(')')) throw new RuntimeException("Missing ')'");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (Character.isLetter(ch)) {
                    while (Character.isLetter(ch)) nextChar();
                    String func = str.substring(startPos, this.pos);
                    double arg;
                    if (eat('(')) {
                        arg = parseExpression();
                        if (!eat(')')) throw new RuntimeException("Missing ')' after " + func);
                    } else {
                        arg = parseFactor();
                    }
                    switch (func.toLowerCase()) {
                        case "sin": x = Math.sin(Math.toRadians(arg)); break;
                        case "cos": x = Math.cos(Math.toRadians(arg)); break;
                        case "tan": x = Math.tan(Math.toRadians(arg)); break;
                        case "log": x = Math.log10(arg); break;
                        case "ln": x = Math.log(arg); break;
                        case "sqrt": x = Math.sqrt(arg); break;
                        default: throw new RuntimeException("Unknown function: " + func);
                    }
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor());
                if (eat('!')) x = factorial(x);
                return x;
            }

            double factorial(double n) {
                if (n < 0) throw new RuntimeException("Invalid factorial");
                double result = 1;
                for (int i = 1; i <= (int) n; i++) result *= i;
                return result;
            }
        }.parse();
    }
}
