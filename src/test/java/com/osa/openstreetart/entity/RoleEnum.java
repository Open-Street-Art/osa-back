public enum RoleEnum {
	VISITOR(1),
	USER(2),
	ARTIST(3),
	ADMINISTRATOR(4);
	
	private int user;
	
	private RoleEnum(int level) {
		user = level;
	}
	
	public int getLevel() {
		return user;
	}

	public void setLevel(int level) {
		if ((level > 0) && (level <= 4 )) {
			user = level;
		}
	}
	
	public String toString() {
		return this.name().toLowerCase();
	}
}
