package cn.edu.swust.custom.dazong;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
/**
 * 通过配置文件中配置方式来注入
 * @author pery
 *
 */
public class DbDao {
	@Autowired
	private SqlSession sqlSession;
	public void setSqlSession(SqlSession sqlSession)throws Exception {
		this.sqlSession = sqlSession;
	}
	
	public int insertIntoItem(List<Item> itemList)throws Exception {
		return sqlSession.insert("depaItem.insertIntoItem", itemList);
	}
	public int insertIntoUserItem(List<UserItem> userItemList)throws Exception {
		return sqlSession.insert("depaItem.insertIntoUserItem", userItemList);
	}

	public List<Long> selectUsers() {
		return sqlSession.selectList("depaItem.selectUsers");
	}

	public List<Long> selectItems() {
		return sqlSession.selectList("depaItem.selectItems");
	}
}
