import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class yrbapp3 {
	private Connection conDB; // connection to the database
	private String url; // URL of database

	private enum types {
		CID_CHECK, CAT_CHECK, CUSTOMER_UPDATE, BOOK_CHECK, PURCHASE, COMPLETE

	};

	public yrbapp3() {
		try {
			// Register driver with DriverManager.
			Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (InstantiationException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.exit(0);
		}

		this.Set_Url("jdbc:db2:c3421a"); // Setting the Address to YRB DB
		this.Initialize_connection(this.url); // initialize the connection.

	}

	// setter for url field
	public void Set_Url(String url) {
		this.url = url;
	}
	// initialize connection

	public void Initialize_connection(String url) {
		try {

			conDB = DriverManager.getConnection(this.url);
		} catch (SQLException e) {
			System.out.print("\nSQL: database connection error.\n");
			System.out.println(e.toString());
			System.exit(0);
		}
	}

	// turn on commit
	private void Turn_commit_on() {
		try {
			conDB.setAutoCommit(true);
		} catch (SQLException e) {
			System.out.print("\nFailed trying to turn autocommit on.\n");
			e.printStackTrace();
			System.exit(0);
		}
	}

	// Turn Off Commit

	private void Turn_commit_off() {
		try {
			conDB.setAutoCommit(false);
		} catch (SQLException e) {
			System.out.print("\nFailed trying to turn autocommit off.\n");
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void run() {
		String input;
		short cid = 0; // store customer Cid
		String cat = null; // Store category chosen by the cust
		boolean done = false;
		Book book = new Book("", (short) -1, "", "", (short) -1);
		double price = 0;
		short qnty = 0;
		double totalcost = 0;
		StringBuilder club = new StringBuilder();
		StringBuilder name = new StringBuilder();
		StringBuilder city = new StringBuilder();
		types type = types.CID_CHECK;
		boolean valid = false;
		Scanner scan = new Scanner(System.in);
		Map<Integer, String> cat_map = new TreeMap<Integer, String>();
		// for selection of categories
		Map<Integer, Book> book_map = new TreeMap<Integer, Book>();
		// for selection of books

		System.out.println("Hello and welcome to Search and Purchase application ");
		System.out.println("Follow these instructions ");
		System.out.println(
				"\"exit\" is a restricted word, any time you enter \"exit\", the application will terminate, any modification done will be committed");
		System.out.println("books and categories are case senstive");
		System.out.println(
				"For yes or no questions, you can enter ( \"yes\" or \"y\" ) for yes, anything else will be treated as no  ");

		while (!done) {
			switch (type) {
			case CID_CHECK:
				System.out.println("Enter Customer Cid: ");
				if (scan.hasNext()) {
					input = scan.nextLine();
					// check if input is of type short
					if (valid_cid_input(input)) {
						// Parse the String to a Short
						cid = Short.parseShort(input);
						// Trying to find Cid of the customer
						if (find_cid(cid, name, city)) {
							// if the Cid exist in the yrb_customer quit this
							// loop, else keep asking

							System.out.println("cid :  " + cid);
							System.out.println("name : " + name);
							System.out.println("city : " + city);

							// Reinitialize
							name = new StringBuilder();
							city = new StringBuilder();

							type = types.CUSTOMER_UPDATE;
						} else {
							System.out.println("Do you want to try again (y/n) ? ");
							input = scan.nextLine();
							// handle user input
							if (!this.handle_yes_No(input)) {
								done = true;// exist the System
								input = null;
							} else {
								type = types.CID_CHECK;
							}
						}
					}
				}
				break;
			case CUSTOMER_UPDATE:
				System.out.println("Do you want to update Customer information (y/n) ? ");
				input = scan.nextLine();
				if (this.handle_yes_No(input)) {
					System.out.println("Do you want to update Customer name (y/n) ? ");
					input = scan.nextLine();
					if (this.handle_yes_No(input)) {
						System.out.println("Enter Customer name : ");
						input = scan.nextLine();
						if (this.valid_name(input)) {
							this.update_customer_name((short) cid, input);
						} else {
							while (!valid) {
								System.out.println("Wrong input !");
								System.out.println("Do you want to try again (y/n) ? ");
								input = scan.nextLine();
								if (this.handle_yes_No(input)) {
									System.out.println("Enter Customer name : ");
									input = scan.nextLine();
									if (this.valid_name(input)) {
										this.update_customer_name((short) cid, input);
										valid = true;
									}

								} else {
									valid = true;
								}
							}
						}
					}
					valid = false;
					System.out.println("Do you want to update Customer city (y/n) ? ");
					input = scan.nextLine();
					if (this.handle_yes_No(input)) {
						System.out.println("Enter the city for the customer : ");
						input = scan.nextLine();
						if (this.valid_city(input)) {
							this.update_customer_address(input, cid);
						} else {
							while (!valid) {
								System.out.println("Do you want to try again (y/n) ? ");
								input = scan.nextLine();
								if (this.handle_yes_No(input)) {
									System.out.println("Enter a city for the customer  : ");
									input = scan.nextLine();
									if (this.valid_city(input)) {
										this.update_customer_address(input, cid);
										valid = true;
									}

								} else {
									valid = true;
								}
							}
						}
					}
					type = types.CAT_CHECK;

				} else {
					type = types.CAT_CHECK;
				}
				break;

			case CAT_CHECK:
				System.out.println();
				// accessing and printing all categories
				this.print_categories();

				System.out.println("\n please choose a category : ");

				// read user input
				input = scan.nextLine();
				// checking if the category that user inputed is in yrb_category
				if (this.valid_category(input)) {
					type = types.BOOK_CHECK;
					cat = input;

				} else {
					System.out.println("The category " + input + " doesn't exist in yrb_cateogry");
					System.out.println("Do you want to try again (y/n)?");
					input = scan.nextLine();
					if (!this.handle_yes_No(input)) {
						System.out.println(" Would you like to go back to Cid selection ?");
						input = scan.nextLine();
						if (!this.handle_yes_No(input)) {
							// you didn't want to re-chose
							System.out.println(
									"You didn't re-chose a category and you don't want to go back to cid selection, the System will exit now, Bye !");
							done = true;
							input = null;
						} else {
							type = types.CID_CHECK;
						}
					} else {
						type = types.CAT_CHECK;
					}
				}
				break;
			case BOOK_CHECK:
				if (!this.books_in_category(cat)) {
					System.out.println("The Category does not contain any books");
					System.out.println("Would you like to go back to Category selection (y/n)");
					input = scan.nextLine();
					if (!this.handle_yes_No(input)) {
						System.out.println("Would you like to go back to cid selection (y/n)");
						input = scan.nextLine();
						if (!this.handle_yes_No(input)) {
							System.out.println(
									"You didn't want to go back to category selection or Cid slecetion, the System will exit now");
							done = true;
							input = null;
						} else {
							type = types.CID_CHECK;
						}
					} else {
						type = types.CAT_CHECK;
					}
				} else {
					System.out.println("\n Choose a book title or index");
					book_map = this.Reterive_And_print_books(cat);
					input = scan.nextLine();
					for (Book b : book_map.values()) {
						if (b.getTitle().equals(input)) {

							// Saving the book
							book = b;
							type = types.PURCHASE;
						}
					}
					// if this is false means that book was found already
					if (type != types.PURCHASE) {
						try {
							book = book_map.get(Integer.parseInt(input));
							// this is called to make sure that book is not
							// null, this will happen if the index the user
							// passed is invalid
							book.getTitle();
							type = types.PURCHASE;
						} catch (NumberFormatException | NullPointerException e) {
							System.out.println(
									"Your input : " + input + " is not a title of a book and is not an index ");
							System.out.println("Do you want to go back to book selection (y/n) ");
							input = scan.nextLine();
							if (!this.handle_yes_No(input)) {
								System.out.println("Do you want to go back to Category selection (y/n)");
								input = scan.nextLine();
								if (!this.handle_yes_No(input)) {
									System.out.println("Do you want to go back to cid selection (y/n)");
									input = scan.nextLine();
									if (!this.handle_yes_No(input)) {
										System.out.println(
												"You didn't want to go back to category selection or Cid slecetion, the System will exit now");
										done = true;
										input = null;
									} else {
										type = types.CID_CHECK;
									}
								} else {
									type = types.CAT_CHECK;
								}

							} else {
								type = types.BOOK_CHECK;
							}

						}

					}

				}
				break;
			case PURCHASE:
				price = this.find_book_min_price(cid, book, club);
				System.out.println("The minimum  price is : " + String.format("%.2f", price));
				System.out.println("Enter the quantity you would like to purchase ");
				input = scan.nextLine();
				if (this.valid_quantity(input)) {
					qnty = Short.parseShort(input);
					totalcost = price * qnty;
					System.out.println("Total cost is : " + qnty + " * " + String.format("%.2f", price) + " = "
							+ String.format("%.2f", totalcost));
					type = types.COMPLETE;
				} else {
					club = new StringBuilder();
					System.out.println("Do you want to try again (y/n)? ");
					input = scan.nextLine();
					if (!this.handle_yes_No(input)) {
						System.out.println("Do you want to go back to book selection (y/n) ");
						input = scan.nextLine();
						if (!this.handle_yes_No(input)) {
							System.out.println("Do you want to go back to Category selection (y/n)");
							input = scan.nextLine();
							if (!this.handle_yes_No(input)) {
								System.out.println("Do you want to go back to cid selection (y/n)");
								input = scan.nextLine();
								if (!this.handle_yes_No(input)) {
									System.out.println(
											"You didn't want to go back to category selection or Cid selection, the System will exit now");
									done = true;
									input = null;
								} else {
									type = types.CID_CHECK;
								}
							} else {
								type = types.CAT_CHECK;
							}

						} else {
							type = types.BOOK_CHECK;
						}
					}

				}
				break;
			case COMPLETE:
				System.out.println("Do you want to purchase : " + book.getTitle() + " with price of : "
						+ String.format("%.2f", price) + " and quantity of : " + qnty + " and total cost of : "
						+ String.format("%.2f", totalcost) + "  (y/n)?");
				input = scan.nextLine();
				if (this.handle_yes_No(input)) {
					this.insert_purchase(cid, club.toString(), book.getTitle(), book.getYear(), qnty);
					System.out.println("Do you want to restart the program (y/n)?");
					input = scan.nextLine();
					if (this.handle_yes_No(input)) {
						type = types.CID_CHECK;
					} else {
						System.out.println(
								"The system will exit now, any previous modification done to the database will be committed");
						done = true;
					}

				} else {
					System.out.println("Do you want to restart the program (y/n)?");
					input = scan.nextLine();
					if (this.handle_yes_No(input)) {
						System.out.println("-----------------------------------------------------------------");
						type = types.CID_CHECK;
					} else {
						System.out.println(
								"The system will exit now, any previous modification done to the database will be committed");
						done = true;
					}
				}
				club = new StringBuilder();
				break;
			default:
				break;

			}
		}
	}

	private boolean valid_city(String input) {
		if (input.length() > 15) {
			System.out.println("Your input : " + input + " is too large, the length should be in the range of [1,15]");
			return false;
		} else {
			return true;
		}

	}

	private boolean valid_name(String input) {
		if (input.length() > 20) {
			System.out.println("Your input : " + input + " is too large, the length should be in the range of [1,20]");
			return false;
		} else {
			return true;
		}
	}

	private void insert_purchase(short cid, String club, String title, short year, short qnty) {
		String queryText = ""; // The SQL text.
		PreparedStatement querySt = null; // The query handle.

		queryText = "INSERT INTO yrb_purchase (cid , club , title , year , when , qnty ) "
				+ " Values ( ? , ? , ? , ?, ? ,? ) ";

		// Prepare the query.
		try {
			querySt = conDB.prepareStatement(queryText);
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in prepare");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Execute the query.
		try {
			querySt.setShort(1, cid);
			querySt.setString(2, club);
			querySt.setString(3, title);
			querySt.setShort(4, year);
			querySt.setTimestamp(5, new java.sql.Timestamp(System.currentTimeMillis()));
			querySt.setShort(6, qnty);

			querySt.execute();
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in execute");
			System.out.println(e.toString());
			System.exit(0);
		}

	}

	// prints all categories
	public void print_categories() {

		String queryText = ""; // The SQL text.
		PreparedStatement querySt = null; // The query handle.
		ResultSet answers = null;

		queryText = "SELECT * From yrb_category ";

		try {
			querySt = conDB.prepareStatement(queryText);
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in prepare");
			System.out.println(e.toString());
			System.exit(0);
		}

		try {
			answers = querySt.executeQuery();
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in execute");
			System.out.println(e.toString());
			System.exit(0);
		}
		System.out.println("Here are the categories ");
		System.out.format("%12s%n", "Categories");
		System.out.format("%12s%n", "-------------------");
		try {
			while (answers.next()) {

				System.out.format("%12s%n", answers.getString("cat"));
			}
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in cursor.");
			System.out.println(e.toString());
			System.exit(0);
		}

		try {
			answers.close();
		} catch (SQLException e) {
			System.out.print("SQL#1 failed closing cursor.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

		try {
			querySt.close();
		} catch (SQLException e) {
			System.out.print("SQL#1 failed closing the handle.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

	}

	private double find_book_min_price(short cid, Book book, StringBuilder club) {
		String queryText = ""; // The SQL text.
		PreparedStatement querySt = null; // The query handle.
		ResultSet answers = null;
		float result = 0;

		queryText = "Select m.cid , m.club , o.title , o.price From yrb_offer o, yrb_member m  "
				+ "Where m.cid = ? and m.club = o.club and   o.title= ? and o.year =  ? " + "and o.price <= ALL "
				+ "( Select price from yrb_offer o1 , yrb_member m1 Where o1.title = ? and o1.year = ? and m1.cid = ? and m1.club = o1.club  )";

		try {
			querySt = conDB.prepareStatement(queryText);
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in prepare");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Execute the query.
		try {
			querySt.setShort(1, cid);
			querySt.setString(2, book.getTitle());
			querySt.setShort(3, book.getYear());
			querySt.setString(4, book.getTitle());
			querySt.setShort(5, book.getYear());
			querySt.setShort(6, cid);
			answers = querySt.executeQuery();
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in execute");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Any answer?
		try {
			if (answers.next()) {
				club = club.append(answers.getString("club"));
				result = Float.parseFloat(String.format("%.2f", answers.getFloat(4)));

			}
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in cursor.");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Close the cursor.
		try {
			answers.close();
		} catch (SQLException e) {
			System.out.print("SQL#1 failed closing cursor.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

		// done with the handle.
		try {
			querySt.close();
		} catch (SQLException e) {
			System.out.print("SQL#1 failed closing the handle.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

		return result;

	}

	private TreeMap<Integer, Book> Reterive_And_print_books(String cat) {
		String queryText = ""; // The SQL text.
		PreparedStatement querySt = null; // The query handle.
		ResultSet answers = null;
		TreeMap<Integer, Book> map = new TreeMap<Integer, Book>();
		int x = 1;
		queryText = this.find_book();

		try {
			querySt = conDB.prepareStatement(queryText);
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in prepare");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Execute the query.
		try {
			querySt.setString(1, cat);
			answers = querySt.executeQuery();
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in execute");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Any answer?
		System.out.format("%7s%27s%7s%12s%12s%n", "index", "title", "year", "language", "weight");
		try {
			while (answers.next()) {
				map.put(x, new Book(answers.getString("title"), answers.getShort("year"), answers.getString("language"),
						answers.getString("cat"), answers.getShort("weight")));
				System.out.format("%7s%27s%7s%12s%12s%n", x, answers.getString("title"), answers.getShort("year"),
						answers.getString("language"), answers.getShort("weight"));
				x++;
			}
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in cursor.");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Close the cursor.
		try {
			answers.close();
		} catch (SQLException e) {
			System.out.print("SQL#1 failed closing cursor.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

		// done with the handle.
		try {
			querySt.close();
		} catch (SQLException e) {
			System.out.print("SQL#1 failed closing the handle.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

		return map;

	}

	// check if cat is not empty
	private boolean books_in_category(String cat) {
		String queryText = ""; // The SQL text.
		PreparedStatement querySt = null; // The query handle.
		ResultSet answers = null;
		boolean result = false;
		queryText = "SELECT *  FROM yrb_book   WHERE cat = ? ";

		if (!this.valid_category(cat)) {
			return false;
		}

		try {
			querySt = conDB.prepareStatement(queryText);
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in prepare");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Execute the query.
		try {
			querySt.setString(1, cat);
			answers = querySt.executeQuery();
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in execute");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Any answer?
		try {
			if (answers.next()) {

				result = true;
			}
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in cursor.");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Close the cursor.
		try {
			answers.close();
		} catch (SQLException e) {
			System.out.print("SQL#1 failed closing cursor.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

		try {
			querySt.close();
		} catch (SQLException e) {
			System.out.print("SQL#1 failed closing the handle.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

		return result;

	}

	// Check if a category exist in YRB_CATEGORY(true) else false
	private boolean valid_category(String cat) {
		String queryText = ""; // The SQL text.
		PreparedStatement querySt = null; // The query handle.
		ResultSet answers = null;
		boolean result = false;
		queryText = "SELECT *  FROM  yrb_category   ";
		try {
			querySt = conDB.prepareStatement(queryText);
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in prepare");
			System.out.println(e.toString());
			System.exit(0);
		}

		try {
			answers = querySt.executeQuery();
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in execute");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Any answer?
		try {
			while (answers.next()) {
				if (answers.getString("cat").equals(cat)) {
					result = true;
				}

			}
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in cursor.");
			System.out.println(e.toString());
			System.exit(0);
		}

		//
		try {
			answers.close();
		} catch (SQLException e) {
			System.out.print("SQL#1 failed closing cursor.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

		try {
			querySt.close();
		} catch (SQLException e) {
			System.out.print("SQL#1 failed closing the handle.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

		return result;

	}

	// check if qnty is in right format (type short: 5 digit or less)
	private boolean valid_quantity(String in) {
		try {
			Short.parseShort(in);
			return true;
		} catch (NumberFormatException e) {
			System.out
					.println("Your input " + in + " is not a valid Quantity the maximum is: [" + Short.MAX_VALUE + "]");
			return false;
		}
	}

	// Update cust name in the YRB DB
	public void update_customer_name(short cid, String name) {

		String queryText = ""; // The SQL text.
		PreparedStatement querySt = null; // The query handle.

		queryText = "UPDATE yrb_customer SET  name  = ?  Where cid =  ? ";

		try {
			querySt = conDB.prepareStatement(queryText);
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in prepare");
			System.out.println(e.toString());
			System.exit(0);
		}

		try {
			querySt.setString(1, name);
			querySt.setShort(2, cid);
			querySt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in execute");
			System.out.println(e.toString());
			System.exit(0);
		}

	}

	// Update customer address in the YRB DB
	private void update_customer_address(String address, short cid) {

		String queryText = ""; // The SQL text.
		PreparedStatement querySt = null; // The query handle.

		queryText = "UPDATE yrb_customer SET  city  = ?  Where cid =  ? ";

		try {
			querySt = conDB.prepareStatement(queryText);
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in prepare");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Execute the query.
		try {
			querySt.setString(1, address);
			querySt.setInt(2, cid);
			querySt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in execute");
			System.out.println(e.toString());
			System.exit(0);
		}

	}

	// Checks if the Cid is in right format (type short)
	public boolean valid_cid_input(String input) throws NumberFormatException {
		try {
			Short.parseShort(input);
			return true;
		} catch (NumberFormatException e) {
			if (input.toLowerCase().equals("exit")) {
				System.exit(0);
			} else {
				System.out.println("Your input " + input + " is not valid");
			}
			return false;
		}

	}

	// Search yrb_customer for the given Cid
	public boolean find_cid(short cid, StringBuilder name, StringBuilder city) {
		String queryText = ""; // The SQL text.
		PreparedStatement querySt = null; // The query handle.
		ResultSet answers = null; // A cursor.

		boolean inDB = false; // Return.

		queryText = "SELECT * " + "FROM yrb_customer " + "WHERE cid = ?     ";

		// Prepare the query.
		try {
			querySt = conDB.prepareStatement(queryText);
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in prepare");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Execute the query.
		try {
			querySt.setInt(1, cid);
			answers = querySt.executeQuery();
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in execute");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Any answer?
		try {
			if (answers.next()) {
				inDB = true;
				name = name.append(answers.getString("name"));
				city = city.append(answers.getString("city"));

			} else {
				inDB = false;
				System.out.println("The input " + cid + " does not exsit ");
			}
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in cursor.");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Close the cursor.
		try {
			answers.close();
		} catch (SQLException e) {
			System.out.print("SQL#1 failed closing cursor.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

		// done with the handle.
		try {
			querySt.close();
		} catch (SQLException e) {
			System.out.print("SQL#1 failed closing the handle.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

		return inDB;

	}

	// handles input of the user for question "Do you want to try again (y/n)

	public boolean handle_yes_No(String input) {
		if (input == null) {
			throw new NullPointerException("Given input is null.");
		}

		String lower = input.toLowerCase();

		if (lower.equals("yes") || lower.equals("y")) {
			return true;
		} else if (lower.equals("exit")) {
			System.exit(0);
		}
		return false;
	}

	// Queries

	public String find_customer() {
		return "SELECT * " + "FROM yrb_customer " + "Where cid = ?";
	}

	public String update_customer_name() {
		return "UPDATE yrb_customer SET  name  = ?  Where cid =  ? ";
	}

	public String find_book() {
		return "SELECT *  FROM yrb_book  WHERE  cat = ? ";
	}

	public String min_price() {
		return " Select m.cid m.club o.title o.price From yrb_offer o, yrb_member m  Where m.cid = ? and m.club = o.club  o.title= ? and o.year=  ? and o.price <= ALL ( Select price from yrb_offer o1 , yrb_member m1 Where o1.title = ? and o1.year = ? and m1.cid = ? and m1.club = o1.club  )";
	}

	public String insert_purchase() {
		return "INSERT INTO yrb_purchase (cid , club , title , when , year , qnty ) "
				+ " Values ( ? , ? , ? , ?, ? ,? ) ";

	}

	public static void main(String[] args) {
		yrbapp3 yrb = new yrbapp3();
		yrb.run();

	}

}
