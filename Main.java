
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;

class Loan {
    double principal;
    double annualRate;
    int months;

    Loan(double principal, double annualRate, int months) {
        this.principal = principal;
        this.annualRate = annualRate;
        this.months = months;
    }

    double calculateEMI() {
        double monthlyRate = annualRate / 12 / 100;

        if (monthlyRate == 0) {
            return principal / months;
        }

        double power = Math.pow(1 + monthlyRate, months);

        return principal * monthlyRate * power / (power - 1);
    }
}

class Payment {
    int month;
    double emi;
    double interest;
    double principalPaid;
    double balance;

    Payment(int month, double emi, double interest,
            double principalPaid, double balance) {

        this.month = month;
        this.emi = emi;
        this.interest = interest;
        this.principalPaid = principalPaid;
        this.balance = balance;
    }
}

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("===== LOAN EMI CALCULATOR =====");

        System.out.print("Enter loan amount: ");
        double amount = sc.nextDouble();

        System.out.print("Enter annual interest rate (%): ");
        double rate = sc.nextDouble();

        System.out.print("Enter loan duration (months): ");
        int months = sc.nextInt();

        if (amount <= 0 || rate < 0 || months <= 0) {
            System.out.println("Invalid loan details.");
            sc.close();
            return;
        }

        Loan loan = new Loan(amount, rate, months);

        double emi = loan.calculateEMI();
        double balance = amount;
        double totalInterest = 0;

        ArrayList<Payment> schedule = new ArrayList<>();

        double monthlyRate = rate / 12 / 100;

        for (int i = 1; i <= months; i++) {

            double interest = balance * monthlyRate;
            double principalPaid = emi - interest;

            if (i == months) {
                principalPaid = balance;
                emi = principalPaid + interest;
            }

            balance -= principalPaid;

            if (Math.abs(balance) < 0.01) {
                balance = 0;
            }

            totalInterest += interest;

            schedule.add(new Payment(
                i, emi, interest, principalPaid, balance
            ));
        }

        double totalPayment = amount + totalInterest;

        System.out.printf("\nMonthly EMI: Rs. %.2f\n",
                          loan.calculateEMI());

        System.out.printf("Total Interest: Rs. %.2f\n",
                          totalInterest);

        System.out.printf("Total Payment: Rs. %.2f\n",
                          totalPayment);

        System.out.println("\n===== AMORTIZATION SCHEDULE =====");

        System.out.println(
            "Month\tEMI\tInterest\tPrincipal\tBalance"
        );

        for (Payment p : schedule) {
            System.out.printf(
                "%d\t%.2f\t%.2f\t\t%.2f\t\t%.2f\n",
                p.month,
                p.emi,
                p.interest,
                p.principalPaid,
                p.balance
            );
        }

        try {
            FileWriter writer = new FileWriter("loan_schedule.csv");

            writer.write(
                "Month,EMI,Interest,Principal,Balance\n"
            );

            for (Payment p : schedule) {
                writer.write(
                    p.month + "," +
                    String.format("%.2f", p.emi) + "," +
                    String.format("%.2f", p.interest) + "," +
                    String.format("%.2f", p.principalPaid) + "," +
                    String.format("%.2f", p.balance) + "\n"
                );
            }

            writer.close();

            System.out.println(
                "\nSchedule saved to loan_schedule.csv"
            );

        } catch (IOException e) {
            System.out.println("Error saving CSV file.");
        }

        sc.close();
    }
}
