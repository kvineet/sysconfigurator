
package io.kvineet.sysconfigurator;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

public class Application {
	
	
	
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		Injector injector = Guice.createInjector();
		Map<Key<?>, Binding<?>> bindings = injector.getAllBindings();
		bindings.forEach((k, v) -> System.out.println("Key: " + k + "\t value: " + v));
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AppWindow window = injector.getInstance(AppWindow.class);
					window.setFrameVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


}
