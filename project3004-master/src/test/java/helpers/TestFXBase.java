package helpers;
import javafx.scene.*;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.loadui.testfx.GuiTest;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import com.softwareeng.project.client.App;

import java.util.concurrent.TimeoutException;


public class TestFXBase extends ApplicationTest {
	private static boolean isHeadless = false;
	
	static {
		if(isHeadless){
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
        }
		
		try {
			ApplicationTest.launch(App.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    @After
    public void afterEachTest() throws TimeoutException {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    /* Helper method to retrieve Java FX GUI Components */
    public <T extends Node> T find (final  String query){
        return (T) lookup(query).queryAll().iterator().next();
    }
}