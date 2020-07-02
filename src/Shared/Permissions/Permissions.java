package Shared.Permissions;

import java.io.Serializable;
import java.util.EnumSet;

/**
 * Just a wrapper for a EnumSet of the Perm enum also included in the Permissions package.
 *
 * @author Lucas Maldonado n10534342
 */
public class Permissions implements Serializable {

	// Default constructor
	public Permissions(){}

	/***
	 * Constructor that inits from an int
	 * @param data 4 character long int storing permissions based on state
	 */
	public Permissions(int data) {
		if ((data & 0b0001) != 0) {
			addPermission(Perm.CREATE_BILLBOARDS);
		}

		if ((data & 0b0010) != 0) {
			addPermission(Perm.SCHEDULE_BILLBOARDS);
		}

		if ((data & 0b0100) != 0) {
			addPermission(Perm.EDIT_ALL_BILLBOARDS);
		}

		if ((data & 0b1000) != 0) {
			addPermission(Perm.EDIT_USERS);
		}
	}

	/***
	 * Returns the number of permissions this has
	 * @return integer counting permissions user has
	 */
	public int numPermissions() {
		int returnValue = 0;

		if (hasPermission(Perm.CREATE_BILLBOARDS)) {
			returnValue += 1;
		}

		if (hasPermission(Perm.SCHEDULE_BILLBOARDS)) {
			returnValue += 1;
		}

		if (hasPermission(Perm.EDIT_ALL_BILLBOARDS)) {
			returnValue += 1;
		}

		if (hasPermission(Perm.EDIT_USERS)) {
			returnValue += 1;
		}

		return returnValue;
	}

	/***
	 * Converts this permissions to a int for easy serialization
	 * @return an int corresponding to user permissions based on the state of each character in the int
	 */
	public int toInt() {
		int returnValue = 0b0000;

		if (hasPermission(Perm.CREATE_BILLBOARDS)) {
			returnValue += 0b0001;
		}

		if (hasPermission(Perm.SCHEDULE_BILLBOARDS)) {
			returnValue += 0b0010;
		}

		if (hasPermission(Perm.EDIT_ALL_BILLBOARDS)) {
			returnValue += 0b0100;
		}

		if (hasPermission(Perm.EDIT_USERS)) {
			returnValue += 0b1000;
		}

		return returnValue;
	}

	public String toString() {
		return PSet.toString();
	}

	public boolean hasPermission(Perm PermissionToCheck){
		return PSet.contains(PermissionToCheck);
	}

	public boolean addPermission(Perm PermissionToAdd){
		return PSet.add(PermissionToAdd);
	}

	public boolean removePermission(Perm PermissionToRemove){
		return PSet.remove(PermissionToRemove);
	}

	private EnumSet<Perm> PSet = EnumSet.noneOf(Perm.class);


}
