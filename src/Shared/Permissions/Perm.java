package Shared.Permissions;

/**
 * Just a simple enumerator with all of the permissions.
 * I'm not sure what the naming conventions are, please let me know / edit this code if you do know.
 *
 * @author Lucas Maldonado n10534342
 */
public enum Perm {
	/** Users with the Create Billboards permission can create new billboards. and will also be able to edit or delete
	 * any billboards they created, as long as those billboards are not presently scheduled. */
	CREATE_BILLBOARDS,

	/** Users with the Edit All Billboards permission will be able to edit or delete any billboard on the system,
	 * including billboards that are currently scheduled. */
	EDIT_ALL_BILLBOARDS,

	/** Users with the Schedule Billboards permission will be able to schedule billboards to be displayed on the
	 * Viewers. */
	SCHEDULE_BILLBOARDS,

	/** Users with the Edit Users permission (administrators) will be able to access a list of all users and can both
	 * edit any user listed in the system as well as create new users. */
	EDIT_USERS
}
