package cabra.dinero;

import cabra.*;
import javax.swing.*;

public class VaultTabPane extends JTabbedPane {

    private VaultManager vaultManager;
    private VaultPanel vaultPanel;
    private StorePanel storePanel;
    
    public static final int MY_WIDTH =  380;
    public static final int MY_HEIGHT = 280;

    public VaultTabPane(VaultManager vaultManager) {
        this.vaultManager = vaultManager;

        this.vaultPanel = new VaultPanel(vaultManager);
        this.storePanel = new StorePanel(vaultManager);

        addTab("Vault  ", ImageManager.createImageIcon("safe.png"), vaultPanel); //put space between text & icon
        addTab("Store  ", ImageManager.createImageIcon("gift.png"), storePanel);
    }
    
    public VaultPanel getVaultPanel(){ return vaultPanel; }
    public StorePanel getStorePanel(){ return storePanel; }
}