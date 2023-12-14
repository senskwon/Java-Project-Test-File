import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Stack;

public class Calculator extends JFrame {
	private JTextField display;
	private String prev_operation = "";
	GridBagLayout grid = new GridBagLayout();
	GridBagConstraints gbc = new GridBagConstraints();

	Toolkit tk = Toolkit.getDefaultToolkit();
	Dimension screenSize = tk.getScreenSize();

	Stack<Double> numStack = new Stack<>();
	Stack<Character> opStack = new Stack<>();
	String num = "";
	String status;

	public Calculator(String title) {
		super(title);
		setLocation(screenSize.width / 2 - 150, screenSize.height / 2 - 200);
		setSize(300, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		display = new JTextField();
		display.setFont(new Font("고딕", Font.BOLD, 30));
		display.setHorizontalAlignment(JTextField.RIGHT);
		display.setEditable(false);
		display.setBackground(Color.LIGHT_GRAY);
		add(display, BorderLayout.NORTH);

		JPanel buttonPanel = new JPanel(new GridBagLayout());
		String[] buttonLabels = { "C", "←", "%", "÷", "7", "8", "9", "x", "4", "5", "6", "-", "1", "2", "3", "+", "0",
				".", "=" };

		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;

		int x = 0, y = 0;
		for (String label : buttonLabels) {
			JButton button = new JButton(label);
			button.addActionListener(new ButtonActionListener());
			button.setForeground(Color.WHITE);
			button.setBackground(new Color(80, 82, 85));
			button.setFont(new Font("Arial", Font.BOLD, 25));
			button.setFocusPainted(false);

			if (label.matches("[0-9]") || label.equals(".")) {
				button.setBackground(new Color(123, 125, 127));
			} else if (label.equals("C")) {
				button.setBackground(new Color(255, 129, 37));
			}

			if (label.equals("0")) {
				makeFrame(button, x, y, 2, 1);
				x++;
			} else {
				makeFrame(button, x, y, 1, 1);
			}
			x++;
			if (x > 3) {
				x = 0;
				y++;
			}

			buttonPanel.add(button, gbc);
		}

		add(buttonPanel, BorderLayout.CENTER);
		setVisible(true);
	}

	public void makeFrame(JButton c, int x, int y, int w, int h) {
		gbc.gridy = y;
		gbc.gridx = x;
		gbc.gridheight = h;
		gbc.gridwidth = w;
		grid.setConstraints(c, gbc);
	}

	class ButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JButton source = (JButton) e.getSource();
			String buttonText = source.getText();

			if (buttonText.equals("C")) {
				display.setText("");
			} else if (buttonText.equals("=")) {
				String result = Double.toString(calculate(display.getText()));
				display.setText("" + result);
				num = "";
				status = "ok";
			} else if (buttonText.equals("←")) {
				String currentText = display.getText();
				if (currentText.length() > 0) {
					display.setText(currentText.substring(0, currentText.length() - 1));
				}
			} else if (buttonText.matches("[÷+x-]") || buttonText.matches("%")) {
				status = "no";
				if (display.getText().equals("")) {
					display.setText("");
				} else if (prev_operation.matches("[÷+x-]") || prev_operation.matches("%")) {
					String is = display.getText();
					String lastS = is.substring(0, is.length() - 1);
					display.setText("");
					display.setText(lastS + buttonText);

				} else {
					display.setText(display.getText() + buttonText);
				}
			} else {
				if (status == "ok") {
					display.setText("");
				}
				status = "no";
				display.setText(display.getText() + buttonText);
			}
			prev_operation = buttonText;
		}
	}

	private void preprocess(String expression) {
		numStack.clear();
		opStack.clear();

		for (int i = 0; i < expression.length(); i++) {
			char ch = expression.charAt(i); // 2

			if (ch == '-' || ch == '+' || ch == 'x' || ch == '÷' || ch == '%') {
				if (num != "")
					numStack.add(Double.valueOf(num));
				if (!opStack.isEmpty()
						&& (opStack.peek().equals('x') || opStack.peek().equals('÷') || opStack.peek().equals('%'))) {

					double n1 = numStack.pop();
					double n2 = numStack.pop();
					Character oper = opStack.pop();

					if (oper.equals('x')) {
						numStack.add(n2 * n1);
					} else if (oper.equals('%')) {
						if (n1 == 0) {
							JOptionPane.showMessageDialog(this, "0으로 나눌 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
							numStack.clear();
							opStack.clear();
							num = "";
							numStack.add(0.0);
							display.setText("");
						} else {
							numStack.add(n2 % n1);
						}

					} else if (oper.equals('÷')) {
						if (n1 == 0) {
							JOptionPane.showMessageDialog(this, "0으로 나눌 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
							numStack.clear();
							opStack.clear();
							num = "";
							numStack.add(0.0);
							display.setText("");
						} else {
							numStack.add(n2 / n1);
						}
					}
				}
				opStack.add(ch);
				num = "";
			} else {
				num = num + ch;
			}

			if (i == expression.length() - 1) {
				if (ch == '-' || ch == '+' || ch == 'x' || ch == '÷' || ch == '%') {
					opStack.pop();
				}
				if (!opStack.isEmpty()
						&& (opStack.peek().equals('x') || opStack.peek().equals('÷') || opStack.peek().equals('%'))) {
					double n1 = Double.valueOf(num);
					double n2 = numStack.pop();
					Character oper = opStack.pop();
					if (oper.equals('x')) {
						numStack.add(n2 * n1);
					} else if (oper.equals('%')) {
						if (n1 == 0) {
							JOptionPane.showMessageDialog(this, "0으로 나눌 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
							numStack.clear();
							opStack.clear();
							num = "";
							numStack.add(0.0);
							display.setText("");
						} else {
							numStack.add(n2 % n1);
						}

					} else if (oper.equals('÷')) {
						if (n1 == 0) {
							JOptionPane.showMessageDialog(this, "0으로 나눌 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
							numStack.clear();
							opStack.clear();
							num = "";
							numStack.add(0.0);
							display.setText("");
						} else {
							numStack.add(n2 / n1);
						}

					}
				} else {
					if (num != "")
						numStack.add(Double.valueOf(num));
				}
			}
		}
		Collections.reverse(numStack);
		Collections.reverse(opStack);
	}

	private double calculate(String expression) {
		preprocess(expression);

		while (!opStack.isEmpty() && numStack.size() >= 2) {
			double n1 = numStack.pop();
			double n2 = numStack.pop();
			Character op = opStack.pop();

			if (op == '+') {
				numStack.add(n2 + n1);
			}			
			else if (op == '-'){
				numStack.add(n1 - n2);
			}
		}

		double result = numStack.pop();
		result = Math.round(result * 1000) / 1000.0;
		return result;
	}

	public static void main(String[] args) {
		new Calculator("Calculator");
	}
}
