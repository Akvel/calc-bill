package pro.akvel.bill;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Возможно ли цифры билета скомбинировать так, чтобы выражение было равно 100.
 *
 * @author akv
 * @since 03.10.2022
 */
public class Calc {
    public static void main(String[] args) {
        String bill = "123456"; //номер автобусного билета
        findNums(bill, 0, new ArrayList<>());
    }

    /**
     *  Получим все возможные комбинации чисел
     */
    private static void findNums(String bill, int begin, List<Object> row) {
        if (begin >= bill.length()) {
            tryToCalc(row);
            return;
        }
        for (int index = begin + 1; index <= bill.length(); index++) {
            String num = bill.substring(begin, index);
            if (begin == 0) {
                row.add(num);
                findNums(bill, index, row);
                row.remove(row.size() - 1);

                //расчетам так же набор чисел, где первое отрицательно
                row.add("-" + num);
                findNums(bill, index, row);
                row.remove(row.size() - 1);
            } else {
                for (var e : Action.values()) {
                    row.add(e);
                    row.add(num);
                    findNums(bill, index, row);
                    row.remove(row.size() - 1);
                    row.remove(row.size() - 1);
                }
            }
        }
    }


    private static void tryToCalc(List<Object> nums) {
        ArrayList<String> result = new ArrayList<>();
        //сгенерим все возможные комбинации порядка вычисления примера
        generateOrder((nums.size() - 1) / 2, "", result);

        ord:
        for (var order : result) {
            var calc = new ArrayList<>();
            var steps = new StringBuilder();
            var prettyRez = new ArrayList<>(nums);
            nums.forEach(it -> {
                if (it instanceof Action) {
                    calc.add(it);
                } else {
                    AtomicInteger holder = new AtomicInteger();
                    holder.set(Integer.parseInt((String) it));
                    calc.add(holder);
                }
            });


            for (int i = 0; i < order.length(); i++) {
                int numericValue = Character.getNumericValue(order.charAt(i));
                int index = numericValue * 2 - 1;

                int num1 = ((AtomicInteger) calc.get(index - 1)).get();
                Action action = (Action) calc.get(index);
                int num2 = ((AtomicInteger) calc.get(index + 1)).get();

                int newVal;
                switch (action) {
                    case PLUS -> newVal = num1 + num2;
                    case MINUS -> newVal = num1 - num2;
                    case DIV -> {
                        if (num2 == 0) {
                            continue ord;
                        }
                        newVal = num1 / num2;

                    }
                    case MULT -> newVal = num1 * num2;
                    default -> throw new IllegalStateException("Unexpected value: " + action);
                }

                //replace all old holder to new one
                var newHolder = ((AtomicInteger) calc.get(index - 1));
                var oldHolder = ((AtomicInteger) calc.get(index + 1));
                newHolder.set(newVal);
                for (int ci = 0; ci < calc.size(); ci++) {
                    if (calc.get(ci) == oldHolder) {
                        calc.set(ci, newHolder);
                    }
                }

                if (i < order.length() - 1) {
                    int i1 = index - 1;
                    while (i1 >= 0) {
                        if (!(calc.get(i1) instanceof Action) && calc.get(i1) != newHolder) {
                            i1 = i1 + 2;
                            break;
                        }
                        i1--;
                    }
                    i1 = Math.max(i1, 0);
                    prettyRez.set(i1, "(" + prettyRez.get(i1));

                    int i2 = index + 1;
                    while (i2 <= calc.size() - 1) {
                        if (!(calc.get(i2) instanceof Action) && calc.get(i2) != newHolder) {
                            i2 = i2 - 2;
                            break;
                        }
                        i2++;
                    }
                    i2 = Math.min(i2, calc.size() - 1);
                    prettyRez.set(i2, prettyRez.get(i2) + ")");

                }


                steps.append(num1).append(action).append(num2).append("=").append(newVal).append(", ");
            }


            int rez = ((AtomicInteger) calc.get(0)).get();

            if (rez == 100) {
                nums.forEach(System.out::print);
                System.out.print("---->");
                prettyRez.forEach(System.out::print);
                System.out.println(" = 100 " + " order:" + order + " steps:" + steps);
            }
        }


    }

    private static void generateOrder(int size, String order, List<String> result) {
        if (size == order.length()) {
            result.add(order);
            return;
        }

        for (int i = 1; i <= size; i++) {
            if (!order.contains("" + i)) {
                generateOrder(size, order + i, result);
            }
        }
    }

    private enum Action {
        PLUS("+"),
        MINUS("-"),
        DIV("/"),
        MULT("*");

        private String sign;

        Action(String s) {
            sign = s;
        }

        @Override
        public String toString() {
            return sign;
        }
    }
}
