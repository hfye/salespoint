package spielwiese.annotation;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: SpecialEntity
 *
 */
@SuppressWarnings("serial")
@Entity
public class SpecialEntity implements Serializable {
	@EmbeddedId
	private SpecialIdentifier id;

	public SpecialEntity() {
		super();
		id = new SpecialIdentifier();
	}
	
	public SpecialIdentifier getIdentifier() {
		return id;
	}
	
	@Override
	public String toString() {
		return id.toString();
	}
}
