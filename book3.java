// class used to store the attributes of the book table from yrb database
public class Book {

	private String title;
	private short year;
	private String language;
	private String cat;
	private short weight;

	// Initializes a new book object
	public Book(String title, short year, String langauge, String category, short weight)
			throws NullPointerException, IllegalArgumentException {

		if (title == null) {
			throw new NullPointerException("Title can't be null ");
		}
		if (title.length() > 25) {
			throw new IllegalArgumentException("Title can only have 25 Character ");
		}
		if (langauge.length() > 10) {
			throw new IllegalArgumentException("Langauge can only have 10 Character ");
		}
		if (category == null) {
			throw new NullPointerException("Category can't be null ");
		}
		if (category.length() > 10) {
			throw new IllegalArgumentException("Category can only have 10 Character");
		}

		this.title = title;
		this.year = year;
		this.language = langauge;
		this.cat = category;
		this.weight = weight;
	}

	// return title of the book
	public String getTitle() {
		return title;
	}

	// return year the book published
	public short getYear() {
		return year;
	}

	// return language of the book
	public String getLanguage() {
		return language;
	}

	// return category that the book belongs to
	public String getCat() {
		return cat;
	}

	// return weight of the book
	public short getWeight() {
		return weight;
	}

	// return Descri of book which includes Title, year, language,cat, weight of the book
	public String toString() {
		return "Title = " + this.title + " year = " + this.year + " language = " + this.language + " Category = "
				+ this.cat + " Weight = " + this.weight;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cat == null) ? 0 : cat.hashCode());
		result = prime * result + ((language == null) ? 0 : language.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + weight;
		result = prime * result + year;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Book other = (Book) obj;
		if (cat == null) {
			if (other.cat != null)
				return false;
		} else if (!cat.equals(other.cat))
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (weight != other.weight)
			return false;
		if (year != other.year)
			return false;
		return true;
	}

}
