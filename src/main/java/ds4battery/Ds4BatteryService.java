package ds4battery;

import java.io.IOException;

import java.lang.ProcessBuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

import org.freedesktop.AbstractDeviceAddedHandler;
import org.freedesktop.UPower;
import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.connections.impl.DBusConnectionBuilder;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.Properties;

public class Ds4BatteryService {
	private List<String> deviceNames = Arrays.asList("gaming_input_ps_controller_battery_", "battery_ps_controller_battery_");
	private PercentagePropertyChangeHandler handler = new PercentagePropertyChangeHandler();

	private boolean isPsController(String name){
		String nm = FilenameUtils.getName(name);

		for(String device : deviceNames){
			if(nm.startsWith(device)){
				return true;
			}
		}

		return false;
	}

	private void processDevice(DBusConnection connection, DBusPath path) throws DBusException {
		String name = path.getPath();

		if(isPsController(name)){
			Properties properties = connection.getRemoteObject("org.freedesktop.UPower", name, Properties.class);
			int value = ((Number) properties.Get("org.freedesktop.UPower.Device", "Percentage")).intValue();

			handler.processBatteryPercentage(name, value);

			connection.addSigHandler(
				Properties.PropertiesChanged.class, 
				connection.getRemoteObject("org.freedesktop.UPower", name, Properties.class), 
				handler
			);
		}
	}

	private void processEvent(DBusConnection connection, UPower.DeviceAdded event){
		try{
			processDevice(connection, event.getDevice());
		}
		catch(DBusException ex){
			System.err.println(String.format("failed to process device - %s", event.getDevice()));

			ex.printStackTrace();
		}
	}	

        public void start() throws DBusException, IOException, InterruptedException {
		try(final DBusConnection connection = DBusConnectionBuilder.forSystemBus().build()){
			UPower service = connection.getRemoteObject("org.freedesktop.UPower", "/org/freedesktop/UPower", UPower.class);
			
			for(DBusPath path : service.EnumerateDevices()){
				processDevice(connection, path);	
			}

			connection.addSigHandler(
				UPower.DeviceAdded.class, 
				connection.getRemoteObject("org.freedesktop.UPower", "/org/freedesktop/UPower", UPower.class), 
				new AbstractDeviceAddedHandler(){
					@Override
					public void handle(UPower.DeviceAdded event){
						processEvent(connection, event);
					}	
				}
			);

			Thread.currentThread().join();
		}
        }
}
