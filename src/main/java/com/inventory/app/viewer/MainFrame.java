//import com.inventory.app.model.enums.Screen;
//
//import javax.swing.*;
//import java.awt.*;
//import java.util.EnumMap;
//import java.util.Map;
//
//public class MainFrame extends JFrame {
//
//    private final CardLayout card = new CardLayout();
//    private final JPanel root = new JPanel(card);
//    private final Map<Screen, JPanel> screens = new EnumMap<>(Screen.class);
//
//    public MainFrame(
//            LoginPanel loginPanel,
//            SupervisorHomePanel supervisorHome,
//            InventoryMenuPanel inventoryMenu,
//            ItemsViewPanel itemsView,
//            JPanel productsView
//    ) {
//        super("Smart Inventory System");
//
//        screens.put(Screen.LOGIN, loginPanel);
//        screens.put(Screen.SUPERVISOR_HOME, supervisorHome);
//        screens.put(Screen.INVENTORY_MENU, inventoryMenu);
//        screens.put(Screen.ITEMS_VIEW, itemsView);
//        screens.put(Screen.PRODUCTS_VIEW, productsView);
//
//        for (Map.Entry<Screen, JPanel> e : screens.entrySet()) {
//            root.add(e.getValue(), e.getKey().name());
//        }
//
//        setContentPane(root);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setSize(1100, 650);
//        setLocationRelativeTo(null);
//
//        show(Screen.LOGIN);
//    }
//
//    public void show(Screen s) {
//        card.show(root, s.name());
//    }
//}
