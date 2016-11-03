package tests;

import com.refactoring.main.ShoppingCart;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ShoppingCartTest {

    @Test
    public void appendFormattedAlignRight() {
        StringBuilder sb = new StringBuilder();

        ShoppingCart.appendFormatted(sb, "some text" , 1, 10);
        assertEquals("Should append string \"some test\"", " some text ", sb.toString());
    }

    @Test
    public void appendFormattedAlignLeft() {
        StringBuilder sb = new StringBuilder();

        ShoppingCart.appendFormatted(sb, "some text" , -1, 20);
        ShoppingCart.appendFormatted(sb, "another text" , -1, 20);
        assertEquals("Should append string \"another test\"", "some text            another text         ", sb.toString());
    }

    @Test
    public void appendFormattedAlignCenter() {
        StringBuilder sb = new StringBuilder();

        ShoppingCart.appendFormatted(sb, "some text" , 0, 10);
        ShoppingCart.appendFormatted(sb, "another text" , 0, 20);
        assertEquals("Should append string \"another test\"", "some text      another text     ", sb.toString());
    }

    @Test
    public void appendFormattedTrimsStringOfTooLongWidth() {
        StringBuilder sb = new StringBuilder();

        ShoppingCart.appendFormatted(sb, "some text" , 1, 5);
        assertEquals("Should append string \"some test\"", "some  ", sb.toString());
    }


    @Test
    public void calculateDiscountNewItem() {
        assertEquals(0, ShoppingCart.calculateDiscount(ShoppingCart.ItemType.NEW, 1));
        assertEquals(0, ShoppingCart.calculateDiscount(ShoppingCart.ItemType.NEW, -1));
        assertEquals(0, ShoppingCart.calculateDiscount(ShoppingCart.ItemType.NEW, 0));
    }

    @Test
    public void calculateDiscountRegularItem() {
        assertEquals(0, ShoppingCart.calculateDiscount(ShoppingCart.ItemType.REGULAR, 1));
        assertEquals(1, ShoppingCart.calculateDiscount(ShoppingCart.ItemType.REGULAR, 10));
        assertEquals(0, ShoppingCart.calculateDiscount(ShoppingCart.ItemType.REGULAR, -1));
    }

    @Test
    public void calculateDiscountSecondFreeItem() {
        assertEquals(0, ShoppingCart.calculateDiscount(ShoppingCart.ItemType.SECOND_FREE, 1));
        assertEquals(51, ShoppingCart.calculateDiscount(ShoppingCart.ItemType.SECOND_FREE, 10));
        assertEquals(0, ShoppingCart.calculateDiscount(ShoppingCart.ItemType.SECOND_FREE, -1));
    }

    @Test
    public void calculateDiscountSaleItem() {
        assertEquals(70, ShoppingCart.calculateDiscount(ShoppingCart.ItemType.SALE, 1));
        assertEquals(71, ShoppingCart.calculateDiscount(ShoppingCart.ItemType.SALE, 10));
        assertEquals(70, ShoppingCart.calculateDiscount(ShoppingCart.ItemType.SALE, -1));
    }

    @Test
    public void formatTicket() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Apple", 0.99, 5, ShoppingCart.ItemType.NEW);
        cart.addItem("Banana", 20.00, 4, ShoppingCart.ItemType.SECOND_FREE);
        cart.addItem("A long piece of toilet paper", 17.20, 1, ShoppingCart.ItemType.SALE);
        cart.addItem("Nails", 2.00, 500, ShoppingCart.ItemType.REGULAR);
        assertEquals("# Item                          Price Quan. Discount   Total \n" +
                "------------------------------------------------------------\n" +
                "1 Apple                          $.99     5        -   $4.95 \n" +
                "2 Banana                       $20.00     4      50%  $40.00 \n" +
                "3 A long piece of toilet paper $17.20     1      70%   $5.16 \n" +
                "4 Nails                         $2.00   500      50% $500.00 \n" +
                "------------------------------------------------------------\n" +
                "4                                                    $550.11 ", cart.formatTicket());
    }
}
