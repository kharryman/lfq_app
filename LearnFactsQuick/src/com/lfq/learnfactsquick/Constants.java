package com.lfq.learnfactsquick;

public class Constants {
	static public class tables{
		//static public String acrostic_files = "acrostic_files";
		//static public String alphabettables = "alphabettables";
		static public String dictionarya = "dictionarya";
		static public String acrostics = "acrostics";
		static public String mnemonics = "mnemonics";
		static public String events_table = "events_table";
		static public String global_number_table = "global_number_table";
		static public String user_review_times = "user_review_times";
		static public String alphabettables = "alphabettables";
		static public String alphabetlists = "alphabetlists";
		static public String user_events_table = "user_events_table";
		static public String user_numbers_table = "user_numbers_table";
		static public String sync_table = "sync_table";		
	}
	static public class cols{
		static public class dictionarya{
			static public String _id = "_id";
			static public String Word = "Word";
			static public String PartSpeech = "PartSpeech";
			static public String Definition = "Definition";
			static public String Number = "Number";			
		}
		static public class mnemonics{
			static public String _id = "_id";
			static public String  Category = "Category";
			static public String  Entry_Number = "Entry_Number";
			static public String  Entry_Index = "Entry_Index";
			static public String  Entry = "Entry";
			static public String  Entry_Mnemonic = "Entry_Mnemonic";
			static public String  Entry_Info = "Entry_Info";
			static public String  Mnemonic_Type = "Mnemonic_Type";
			static public String  Title = "Title";
			static public String  Is_Linebreak = "Is_Linebreak";
		}		
		static public class global_number_table{
			static public String _id = "_id";
			static public String Number = "Number";
			static public String NumInf = "NumInf";
			static public String NumWors = "NumWors";
			static public String Type = "Type";
		}
		static public class user_numbers_table{
			static public String _id = "_id";
			static public String Number = "Number";
			static public String NumInf = "NumInf";
			static public String NumWors = "NumWors";
			static public String Type = "Type";
		}		
		static public class user_review_times{
			static public String _id = "_id";
			static public String UserName = "UserName";
			static public String Time1 = "Time1";
			static public String Time2 = "Time2";
			static public String Time3 = "Time3";
			static public String Time4 = "Time4";
			static public String Time5 = "Time5";
			static public String Time6 = "Time6";
			static public String Time7 = "Time7";
			static public String Time8 = "Time8";
			static public String Time9 = "Time9";
			static public String Time10 = "Time10";
		}
		static public class acrostics{
			static public String _id = "_id";
			static public String Name = "Name";
			static public String Information = "Information";
			static public String Acrostics = "Acrostics";
			static public String Mnemonics = "Mnemonics";
			static public String Peglist = "Peglist";
			static public String Image = "Image";
			static public String Has_Image = "Has_Image";
		}
		static public class alphabetlists{
			static public String _id = "_id";
			static public String A = "A";
			static public String B = "B";
			static public String C = "C";
			static public String D = "D";
			static public String E = "E";
			static public String F = "F";
			static public String G = "G";
			static public String H = "H";
			static public String I = "I";
			static public String J = "J";
			static public String K = "K";
			static public String L = "L";
			static public String M = "M";
			static public String N = "N";
			static public String O = "O";
			static public String P = "P";
			static public String Q = "Q";
			static public String R = "R";
			static public String S = "S";
			static public String T = "T";
			static public String U = "U";
			static public String V = "V";
			static public String W = "W";
			static public String X = "X";
			static public String Y = "Y";
			static public String Z = "Z";
		}
		static public class sync_table{		
			static public String _id = "_id";
			static public String id = "id";
			static public String Password = "Password";
			static public String SQL = "SQL";
			static public String Action = "Action";
			static public String Device_Id = "Device_Id";
			static public String DB = "DB";
			static public String Username = "Username";
			static public String Is_Image = "Is_Image";
			static public String Image = "Image";
			static public String Table_name = "Table_name";
			static public String Name = "Name";
		}
		static public class events_table{
			static public String _id = "_id";
			static public String Year = "Year";			
			static public String MyDate = "MyDate";
			static public String Event = "Event";
			static public String Acrostics = "Acrostics";
			static public String SaveWords = "SaveWords";
			static public String Type = "Type";
		}
		static public class user_events_table{
			static public String _id = "_id"; 
			static public String Year = "Year";
			static public String MyDate = "MyDate";			
			static public String Event = "Event";
			static public String Acrostics = "Acrostics";
			static public String SaveWords = "SaveWords";
			static public String Type = "Type";
		}
		static public class user_saved_newwords{
			static public String _id = "_id";
			static public String Table_name = "Table_name";
			static public String MyDate = "MyDate";
			static public String Word = "Word";
		}
	}	
}

