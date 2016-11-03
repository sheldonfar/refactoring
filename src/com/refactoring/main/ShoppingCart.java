package com.refactoring.main;
import java.util.*;
import java.text.*;

/**
 * Containing items and calculating price.
 */
public class ShoppingCart {
    public enum ItemType {NEW, REGULAR, SECOND_FREE, SALE}

    private final String[] HEADER = {"#", "Item", "Price", "Quan.", "Discount", "Total"};
    private final int[] ALIGN = new int[]{1, -1, 1, 1, 1, 1};
    private final String LINE_SEPARATOR = "-";

    /**
     * Tests all class methods.
     */
    public static void main(String[] args) {
        // TODO: add tests here
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Apple", 0.99, 5, ItemType.NEW);
        cart.addItem("Banana", 20.00, 4, ItemType.SECOND_FREE);
        cart.addItem("A long piece of toilet paper", 17.20, 1, ItemType.SALE);
        cart.addItem("Nails", 2.00, 500, ItemType.REGULAR);
        System.out.println(cart.formatTicket());
    }

    /**
     * Adds new item.
     *
     * @param title    item title 1 to 32 symbols
     * @param price    item price in USD, > 0
     * @param quantity item quantity, from 1
     * @param type     item type
     * @throws IllegalArgumentException if some value is wrong
     */
    public void addItem(String title, double price, int quantity, ItemType type) {
        if (title == null || title.length() == 0 || title.length() > 32)
            throw new IllegalArgumentException("Illegal title");
        if (price < 0.01)
            throw new IllegalArgumentException("Illegal price");
        if (quantity <= 0)
            throw new IllegalArgumentException("Illegal quantity");

        items.add(new Item(title, price, quantity, type));
    }

    /**
     * Formats shopping price.
     *
     * @return  string as lines, separated with \n,
     *          first line:   # Item                   Price Quan. Discount      Total
     *          second line: ---------------------------------------------------------
     *          next lines:  NN Title                 $PP.PP    Q       DD%     $TT.TT
     *                        1 Some title              $.30    2         -       $.60
     *                        2 Some very long ti... $100.00    1       50%     $50.00
     *                       ...
     *                       31 Item 42              $999.00 1000         - $999000.00
     *          end line:    ---------------------------------------------------------
     *          last line:   31                                             $999050.60
     *
     *          Item title is trimmed to 20 chars adding '...'
     *
     *          if no items in cart returns "No items." string.
     */
    public String formatTicket() {
        if (items.size() == 0) {
            return "No items.";
        }
        List<String[]> lines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        // formatting each line
        double total = 0.00;
        int index = 0;
        for (Item item : items) {
            int discount = calculateDiscount(item.type, item.quantity);
            double itemTotal = item.price * item.quantity * (100.00 - discount) / 100.00;
            lines.add(new String[]{
                    String.valueOf(++index),
                    item.title,
                    MONEY.format(item.price),
                    String.valueOf(item.quantity),
                    (discount == 0) ? "-" : (String.valueOf(discount) + "%"),
                    MONEY.format(itemTotal)
            });
            total += itemTotal;
        }

        int[] width = new int[]{0, 0, 0, 0, 0, 0};
        int lineLength = width.length - 1;

        for (int i = 0; i < HEADER.length; i++) {
            width[i] = Math.max(HEADER[i].length(), longestStringWidthByIndex(lines, i));
            lineLength += width[i];
        }

        buildHeader(sb, width);
        buildSeparator(sb, LINE_SEPARATOR, lineLength, true);
        buildLines(sb, width, lines);
        buildSeparator(sb, LINE_SEPARATOR, lineLength, true);
        buildFooter(sb, width, index, total);

        return sb.toString();
    }

    private void buildHeader(StringBuilder sb, int[] width) {
        for (int i = 0; i < HEADER.length; i++) {
            appendFormatted(sb, HEADER[i], ALIGN[i], width[i]);
        }
        sb.append("\n");
    }

    private void buildLines(StringBuilder sb, int[] width, List<String[]> lines) {
        for (String[] line : lines) {
            for (int i = 0; i < line.length; i++)  {
                appendFormatted(sb, line[i], ALIGN[i], width[i]);
            }
            sb.append("\n");
        }
    }

    private void buildFooter(StringBuilder sb, int[] width, int index, double total) {
        String[] footer = {String.valueOf(index), "", "", "", "", MONEY.format(total)};

        for (int i = 0; i < footer.length; i++) {
            appendFormatted(sb, footer[i], ALIGN[i], width[i]);
        }
    }

    private void buildSeparator(StringBuilder sb, String separator, int count, boolean addNewLine) {
        for (int i = 0; i < count; i++) {
            sb.append(separator);
        }
        if (addNewLine) {
            sb.append("\n");
        }
    }

    private int longestStringWidthByIndex(List<String[]> list, int index) {
        int width = 0;
        for (String[] elem : list) {
            width = Math.max(width, elem[index].length());
        }
        return width;
    }

    // --- private section -----------------------------------------------------
    private static final NumberFormat MONEY;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        MONEY = new DecimalFormat("$#.00", symbols);
    }

    /**
     * Appends to sb formatted value.
     * Trims string if its length > width.
     *
     * @param align -1 for ALIGN left, 0 for center and +1 for ALIGN right.
     */
    public static void appendFormatted(StringBuilder sb, String value, int align, int width) {
        if (value.length() > width)
            value = value.substring(0, width);
        int before = (align == 0)
                ? (width - value.length()) / 2
                : (align == -1) ? 0 : width - value.length();
        int after = width - value.length() - before;
        while (before-- > 0)
            sb.append(" ");
        sb.append(value);
        while (after-- > 0)
            sb.append(" ");
        sb.append(" ");
    }

    /**
     * Calculates item's discount.
     * For NEW item discount is 0%;
     * For SECOND_FREE item discount is 50% if quantity > 1
     * For SALE item discount is 70%
     * For each full 10 not NEW items item gets additional 1% discount, * but not more than 80% total
     */
    public static int calculateDiscount(ItemType type, int quantity) {
        int discount = 0;
        switch (type) {
            case NEW:
                return 0;
            case REGULAR:
                discount = 0;
                break;
            case SECOND_FREE:
                if (quantity > 1)
                    discount = 50;
                break;
            case SALE:
                discount = 70;
                break;
        }
        if (discount < 80) {
            discount += quantity / 10;
            if (discount > 80)
                discount = 80;
        }
        return discount;
    }

    /**
     * item info
     */
    private static class Item {
        String title;
        double price;
        int quantity;
        ItemType type;

        Item(String title, double price, int quantity, ItemType type) {
            this.title = title;
            this.price = price;
            this.quantity = quantity;
            this.type = type;
        }
    }

    /**
     * Container for added items
     */
    private List<Item> items = new ArrayList<>();
}
