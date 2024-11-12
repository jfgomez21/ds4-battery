package ds4battery;

import java.io.IOException;

import java.lang.ProcessBuilder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.freedesktop.dbus.handlers.AbstractPropertiesChangedHandler;
import org.freedesktop.dbus.interfaces.Properties;
import org.freedesktop.dbus.types.Variant;

public class PercentagePropertyChangeHandler extends AbstractPropertiesChangedHandler {
	private Set<String> notified = new HashSet<>();

	private void sendNotification(int battery){
		try{
			new ProcessBuilder("notify-send", "--icon=input-gaming", "Wireless Controller", String.format("Battery - %d%%", battery)).start();
		}
		catch(IOException ex){
			System.err.println("failed to run notify-send");

			ex.printStackTrace();
		}
	}

	public void processBatteryPercentage(String name, int percentage){
		System.out.println(String.format("%s - %d", name, percentage));

		if(percentage <= 10){
			if(!notified.contains(name)){
				sendNotification(percentage);

				notified.add(name);
			}
		}	
		else {
			sendNotification(percentage);

			notified.remove(name);
		}
	}

	@Override
	public void handle(Properties.PropertiesChanged event){
		for(Map.Entry<String, Variant<?>> entry : event.getPropertiesChanged().entrySet()){
			if("Percentage".equals(entry.getKey())){
				String name = event.getPath();
				int percentage = ((Number) entry.getValue().getValue()).intValue();
				
				System.out.println(String.format("propery changed %s - %d", name, percentage));

				if(percentage <= 10){
					processBatteryPercentage(name, percentage);
				}
				else{
					notified.remove(name);
				}
			}
		}
	}
}
