package org.freedesktop;

import java.util.List;

import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.messages.DBusSignal;

public interface UPower extends DBusInterface {
	public List<DBusPath> EnumerateDevices(); 

	class DeviceAdded extends DBusSignal {
		private DBusPath path;

		public DeviceAdded(String _objectPath, DBusPath path) throws DBusException {
			super(_objectPath, path);

			this.path = path;
		}

		public DBusPath getDevice(){
			return path;
		} 
	}
}
