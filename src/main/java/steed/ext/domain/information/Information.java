package steed.ext.domain.information;

import java.util.Date;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import steed.domain.annotation.CleanXss;
import steed.ext.domain.user.User;
import steed.hibernatemaster.domain.BaseRelationalDatabaseDomain;
import steed.util.digest.Base64Util;
/**
 * 资讯实体类
 * @author 战马
 *
 */
@Entity
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class Information extends BaseRelationalDatabaseDomain{
	private static final long serialVersionUID = 7918896356926299852L;

	private Integer id;
	private String title;
	private String title_like_1;
	@CleanXss
	private String content;
	/**
	 * 发布日期
	 */
	private Date publishDate;

	/**
	 * 因为评论可能很多，所以不做一对多映射
	 */
	private Set<Comment> comments;
	private User author;
	private Programa programa;
	
	public Date getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}
	@Transient
	public String getTitle_like_1() {
		return title_like_1;
	}
	public void setTitle_like_1(String title_like_1) {
		this.title_like_1 = title_like_1;
	}
	
	@OneToOne
	public User getAuthor() {
		return author;
	}
	public void setAuthor(User author) {
		this.author = author;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	@OneToOne
	public Programa getPrograma() {
		return programa;
	}

	public void setPrograma(Programa programa) {
		this.programa = programa;
	}
	
	@Id
	@GenericGenerator(name="gen1",strategy="native")
	@GeneratedValue(generator="gen1")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * 因为评论可能很多，所以不做一对多映射
	 */
	@Transient
	public Set<Comment> getComments() {
		return comments;
	}

	public void setComments(Set<Comment> comments) {
		this.comments = comments;
	}
	
	public void base64EncodeContent(){
		setContent(Base64Util.base64Encode(getContent()));;
	}
	public void base64DecodeContent(){
		setContent(Base64Util.base64Encode(getContent()));
	}

	@Override
	public void initializeAll() {
		initialize();
		domainInitializeAll(getAuthor());
		domainInitializeAll(getPrograma());
	}

}
