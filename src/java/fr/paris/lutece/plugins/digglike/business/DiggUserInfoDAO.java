package fr.paris.lutece.plugins.digglike.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

public class DiggUserInfoDAO implements IDiggUserInfoDAO {
	
	private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT first_name,last_name,business_mail,home_mail,login " +
        "FROM digglike_digg_user_info WHERE lutece_user_key = ?  ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO digglike_digg_user_info ( lutece_user_key,first_name,last_name,business_mail,home_mail,login ) " +
        "VALUES(?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM digglike_digg_user_info WHERE lutece_user_key= ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE digglike_digg_user_info SET " +
        "first_name= ?,last_name= ?,business_mail=?,home_mail=?,login= ?" +
        " WHERE lutece_user_key= ? ";

   
    
   
   
    /**
     * {@inheritDoc}
     */
    @Override
	public void insert( DiggUserInfo diggUserInfo, Plugin plugin )
    {
        int ncpt=1;
    	DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        daoUtil.setString( ncpt++, diggUserInfo.getLuteceUserKey() );
        daoUtil.setString( ncpt++, diggUserInfo.getFirstName() );
        daoUtil.setString( ncpt++, diggUserInfo.getLastName() );
        daoUtil.setString( ncpt++, diggUserInfo.getBusinessMail() );
        daoUtil.setString( ncpt++, diggUserInfo.getHomeMail() );
        daoUtil.setString( ncpt++, diggUserInfo.getLogin() );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
	public void update( DiggUserInfo diggUserInfo, Plugin plugin )
    {
        int ncpt=1;
    	DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

       
        daoUtil.setString( ncpt++, diggUserInfo.getFirstName() );
        daoUtil.setString( ncpt++, diggUserInfo.getLastName() );
        daoUtil.setString( ncpt++, diggUserInfo.getBusinessMail() );
        daoUtil.setString( ncpt++, diggUserInfo.getHomeMail() );
        daoUtil.setString( ncpt++, diggUserInfo.getLogin() );
        daoUtil.setString( ncpt++, diggUserInfo.getLuteceUserKey() );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
    

   
    /**
     * {@inheritDoc}
     */
    @Override
	public DiggUserInfo load(  String strLuteceUserKey, Plugin plugin )
    {
    	DiggUserInfo submitUserInfo = null;
        
    	int ncpt=1;
    	
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
      
        daoUtil.setString(1, strLuteceUserKey);
        
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
        	submitUserInfo=new DiggUserInfo();
        	
        	submitUserInfo.setLuteceUserKey(strLuteceUserKey);
        	submitUserInfo.setFirstName(daoUtil.getString(ncpt++));
        	submitUserInfo.setLastName(daoUtil.getString(ncpt++));
        	submitUserInfo.setBusinesMail(daoUtil.getString(ncpt++));
        	submitUserInfo.setHomeMail(daoUtil.getString(ncpt++));
        	submitUserInfo.setLogin(daoUtil.getString(ncpt++));
        	
        }

        daoUtil.free(  );

        return submitUserInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( String strLuteceUserKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setString(1, strLuteceUserKey);
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

  

}
