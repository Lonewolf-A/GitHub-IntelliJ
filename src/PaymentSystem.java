 class PaymentException extends Exception {
     PaymentException(String msg) { super(msg); }
 }

 interface Refundable {
     void refund(double amount) throws PaymentException;
 }

 abstract class Payment {
     protected double amount;
     protected boolean settled = false;
     Payment(double amount) { this.amount = amount; }

     abstract double fee();

     final void settle() throws PaymentException {
         if (amount <= 0) throw new PaymentException("amount must be positive");
         settled = true;
         System.out.printf("%s settled $%.2f (fee $%.2f)%n",
                 getClass().getSimpleName(), amount, fee());
     }
 }

 class CardPayment extends Payment implements Refundable {
     CardPayment(double amount) { super(amount); }
     @Override double fee() { return amount * 0.029 + 0.30; }
     @Override public void refund(double refundAmount) throws PaymentException {
         if (!settled) throw new PaymentException("cannot refund an unsettled payment");
         if (refundAmount > amount) throw new PaymentException("refund exceeds original");
         System.out.printf("Card refunded $%.2f%n", refundAmount);
     }
 }

 class CashPayment extends Payment {
     CashPayment(double amount) { super(amount); }
     @Override double fee() { return 0; }
 }

 public class PaymentSystem {
     public static void main(String[] args) {
         Payment[] payments = { new CardPayment(100), new CashPayment(50), new CardPayment(-5) };
         for (Payment p : payments) {
             try {
                 p.settle();
                 if (p instanceof Refundable r) r.refund(25);
             } catch (PaymentException e) {
                 System.out.println("FAILED: " + e.getMessage());
             }
         }
     }
}
