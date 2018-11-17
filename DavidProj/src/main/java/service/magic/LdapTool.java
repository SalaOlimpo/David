package service.magic;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

import service.utils.Constants;
import service.utils.Constants.LDAP;
import service.utils.Constants.MySQL;

/**
 * Class used to wrap all the LDAP queries
 * 
 * @author	SalaOlimpo
 * @since	24.08.2018
 */
public class LdapTool {

	private InitialLdapContext context;
	
	public LdapTool() throws NamingException {
		context = initialiaseLdapContext();
	}
	public void Close() throws NamingException {
		context.close();
	}

	/**
	 * Performs the LDAP query, returning a single attribute
	 * 
	 * @param userContext	The Base DN
	 * @param filter		The filter
	 * @param attribute		The requested attribute
	 */
	public String searchAttribute(String userContext, String attribute, String filter) throws NamingException {
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration<SearchResult> searchResults = context.search(userContext, filter, searchControls);

		if(searchResults.hasMoreElements()) {
			SearchResult sr = searchResults.next();
			Attribute	 at = sr.getAttributes().get(attribute);
			
			if (at != null) return at.get().toString();
		}
		return null;
	}


	/**
	 * Performs the LDAP query, returning a single attribute
	 * 
	 * @param userContext	The Base DN
	 * @param filter		The filter
	 */
	public String getDN(String userContext, String filter) throws NamingException {
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration<SearchResult> searchResults = context.search(userContext, filter, searchControls);
		 
		if (searchResults.hasMoreElements()) {
			SearchResult sr = searchResults.next();
			return sr.getName() + ',' + LDAP.BASE_DN;
		}
		return null;
	}
	
	
	/**
	 * Initializes the connection
	 */
	private InitialLdapContext initialiaseLdapContext() throws NamingException {
		Properties properties = new Properties();
		properties.put(Context.INITIAL_CONTEXT_FACTORY,	"com.sun.jndi.ldap.LdapCtxFactory");
		properties.put(Context.PROVIDER_URL,			"ldap://" + LDAP.HOST + ":" + LDAP.PORT);
		properties.put(Context.SECURITY_AUTHENTICATION, "simple");
		properties.put(Context.SECURITY_PRINCIPAL,		LDAP.USERNAME); 
		properties.put(Context.SECURITY_CREDENTIALS,	LDAP.PASSWORD);

		return new InitialLdapContext(properties, null);
	}
	 
	/**
	 * Perform a query to find the needed sAMAccountName, based on room
	 * @param room
	 */
	public String findUserByRoom(String room) throws NamingException {
		return searchAttribute(LDAP.BASE_DN, LDAP.FIELD_USER,
				"(" + LDAP.FIELD_ROOM + "=" + room + ")"
		);
	}
	
	/**
	 * Disable / Enable an user
	 * @param user		The user name
	 * @param enable	True if enable, false if disable
	 * @param lang 
	 * @return
	 * @throws Exception 
	 */
	public String toggleUser(String room, String ldapUser, boolean enable, int lang) throws Exception {
		ModificationItem[] mods = new ModificationItem[1];

		int control = Integer.parseInt(
				searchAttribute(LDAP.BASE_DN, LDAP.FIELD_CONT,
					"(" + LDAP.FIELD_ROOM + "=" + room + ")" )
		);
		String dn	= getDN(LDAP.BASE_DN, "(" + LDAP.FIELD_ROOM + "=" + room + ")" );

		if(enable)	control = control & ~2;
		else		control = control |  2;


		Attribute mod	= new BasicAttribute	(LDAP.FIELD_CONT, Integer.toString(control));
		mods[0]			= new ModificationItem	(DirContext.REPLACE_ATTRIBUTE, mod);

		context.modifyAttributes(dn, mods);

		MySqlImpl mysql = new MySqlImpl(MySQL.DB_RHEA);
			mysql.setEnableAction(ldapUser, enable);
		mysql.closeDB();
		
		switch(lang) {
			case Constants.Language.ITALIAN:
				return "Utente " + ldapUser + (enable ? " abilitato" : " disabilitato");
			default:
				return "User " + ldapUser + (enable ? " enabled" : " disabled");
		}
		
	}

}
