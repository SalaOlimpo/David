package service.magic;

import java.net.URL;

import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

import service.utils.Constants;

/**
 * Class used to call VMware API functions
 * 
 * @author	SalaOlimpo
 * @since	24.08.2018
 */
public class VMA {
	
	private static ServiceInstance si;
	
	static {
		try {
			si = new ServiceInstance(new URL(Constants.VMware.URL), Constants.VMware.USER, Constants.VMware.PASS, true);
		} catch (Exception e) {
			// who cares?
		}
	}
	
	@SuppressWarnings("unused")
	public static String handleMachine(String machine, int code) throws Exception  {
		Folder rootFolder = si.getRootFolder();
		VirtualMachine vm = (VirtualMachine) new InventoryNavigator(
	    	      rootFolder).searchManagedEntity("VirtualMachine", machine);
	
		if(code == Constants.VMware.OP_REBOOT) {
			vm.rebootGuest();
			return "Sent reboot request to machine " + machine;
		}
		if(code == Constants.VMware.OP_TURNON) {
			Task task = vm.powerOnVM_Task(null);
			return "Sent power on requesto to machine " + machine;
		}
		if(code == Constants.VMware.OP_TURNOFF) {
			Task task = vm.powerOffVM_Task();
			return "Sent power off requesto to machine " + machine;
		}
		return "An error occurred";
	}
}
