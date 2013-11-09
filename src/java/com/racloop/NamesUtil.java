package com.racloop;

import java.util.ArrayList;
import java.util.Random;

public class NamesUtil {

	public static final int NUMBER_OF_NAMES = 3;
	public static final String LAST_NAMES = "Sharma,Gupta,Agrawal,Bhatacharya,Arora,Singhal,Jain,Kulshreshtha,Jindal,Kapoor,Anand,Punj,Gosain,Sarin," +
			"Handa,Narang,Wadhwa,Mahajan,Patel,Yadav,Gandhi,Seth,Pandey,Sinha,Chawala,Dutta,Duggal,Tiwari,Kumar,Thakur,Shetty,Rawal,Vigh,Goel,Goyal," +
			"Roy,Gill,Modi,Shah,Mehta,Dixit,Nair,Joshi,Ghosh,Pandit,Mishra,Menon,Verma,Patil,Sarkar,Nadar,Naidu,Bhatt,Kaul,Raina,Brar,Negi,Chauhan" +
			"Sandhu,Sidhu,Trivedi,Parikh,Deshmukh,Jaitley,Dhillon,Walia,Kamath,Solanki,Chaturvedi,Sastry,Sahu,Shinde,Jadhav,Agarkar,Dikshit,Bhonsle" +
			"Mittal,Prasad,Rustagi,Bhatia,Sood,Krishnan,Sonwane,Shankar,Sadhwani,Kamble,Jain,Badlani,Kalra,Bhardwaj,Purohit,Pillai,Bansal,Dubey,Dhar";
	public static final String FIRST_NAMES_BOYS = "Aadarsh,Aadesh,Abhay,Abhijeet,Abhilash,Abhinav,Abhishek,Achal,Aditya,Ajay,Ajit,Akaash,Alok,Ambar,Amish," +
			"Amit,Amogh,Anand,Aneesh,Anil,Anirudh,Ankit,Ankur,Anshul,Anuj,Anupam,Anurag,Apurv,Archit,Arjun,Arun,Ashish,Ashok,Ashutosh,Ashwin,Atul,Bhanu," +
			"Bharat,Bhaskar,Bhupendra,Bhupesh,Bhushan,Chaitanya,Chandan,Chandrashekhar,Chandresh,Chetan,Chinmay,Chirag,Chirayu,Daksh,Daman,Darshan,Deepak," +
			"Deepankar,Deependra,Devansh,Devesh,Dheeraj,Dhiren,Dilip,Dinesh,Divit,Eshaan,Gagan,Gaurav,Gautam,Girish,Gopal,Govind,Gulshan,Hardik,Harish,Harman," +
			"Harsh,Hemal,Himanshu,Hitesh,Hrishikesh,Indrajit,Isha,Ishan,Jagadeep,Jagan,Jaidev,Janesh,Jashith,Jatin,Jay,Jayadeep,Jayant,Jeethesh,Kailash,Kamal," +
			"Kanishk,Kapil,Kartik,Kaushik,Ketan,Krishav,Kshitij,Kunal,Kundan,Lalit,Madhav,Mahendra,Mahesh,Mandeep,Mangesh,Manish,Manoj,Manohar,Manu,Mayank," +
			"Mayur,Mehul,Mihir,Milind,Mohit,Mrinal,Mukesh,Mukul,Nakul,Namit,Nandan,Narendra,Neel,Neeraj,Nikhil,Nilay,Nimesh,Niraj,Nirav,Nirmal,Nishant,Nitin," +
			"Nitish,Palash,Pankaj,Parag,Pavan,Pramod,Pranav,Prasanjit,Pratap,Prateek,Praveen,Prem,Priyanshu,Puneet,Rachit,Rajaneesh,Raghav,Rahul,Raj,Rajan";
	public static final String FIRST_NAMES_GIRLS = "Abha,Aditi,Aisha,Akriti,Alisha,Amita,Amrita,Anamika,Ananya,Anchal,Anisha,Anita,Anjali,Anjana,Anju,Ankita," +
			"Anshika,Anshu,Antara,Anubha,Anupama,Anuradha,Anushka,Aparna,Arti,Archana,Arpita,Arushi,Ashima,Asha,Ashita,Ashwini,Ayesha,Bhavana ,Bhoomika,Bijal," +
			"Bindiya,Brinda,Chanchal,Charu,Chetana,Chhaya,Chitra,Damini,Deepali,Deepashikha,Deepika,Deepti,Devika,Diksha,Dipti,Dishita,Ekta,Esha,Garima,Gauri," +
			"Geeta,Gitanjali,Gitika,Hansika,Harsha,Hemali,Indrani,Indu,Ipsa,Ishita,Janhavi,Jaya,Juhi,Jyoti,Kajal,Kamini,Kamna,Kapila,Karuna,Kavita,Kiran,Komal," +
			"Kritika,Kusum";
	public static final ArrayList<String> LAST_NAMES_LIST = new ArrayList<String>();
	public static final ArrayList<String> FIRST_NAMES_BOYS_LIST = new ArrayList<String>();
	public static final ArrayList<String> FIRST_NAMES_GIRLS_LIST = new ArrayList<String>();
	public static final ArrayList<String> FIRST_NAMES_ALL_LIST = new ArrayList<String>();
	
	static {
		String [] tmp = LAST_NAMES.split(",");
		for (String string : tmp) {
			LAST_NAMES_LIST.add(string);
		}
		tmp = FIRST_NAMES_BOYS.split(",");
		for (String string : tmp) {
			FIRST_NAMES_BOYS_LIST.add(string);
		}
		tmp = FIRST_NAMES_GIRLS.split(",");
		for (String string : tmp) {
			FIRST_NAMES_GIRLS_LIST.add(string);
		}
		FIRST_NAMES_ALL_LIST.addAll(FIRST_NAMES_BOYS_LIST);
		FIRST_NAMES_ALL_LIST.addAll(FIRST_NAMES_GIRLS_LIST);
	}
	
	public static ArrayList<String> getRandomBoyNames() {
		return getRandomBoyNames(NUMBER_OF_NAMES);
	}
	
	public static ArrayList<String> getRandomBoyNames(int number) {
		ArrayList<String> boyNames = new ArrayList<String>();
		Random randomGenerator = new Random();
		for (int idx = 1; idx <= number; ++idx){
			int randomInt = randomGenerator.nextInt(FIRST_NAMES_BOYS_LIST.size());
			String firstName = FIRST_NAMES_BOYS_LIST.get(randomInt);
			randomInt = randomGenerator.nextInt(LAST_NAMES_LIST.size());
			String lastName = LAST_NAMES_LIST.get(randomInt);
			String name = firstName + " " + lastName;
			boyNames.add(name);
		}
		return boyNames;
	}
	
	public static ArrayList<String> getRandomNames() {
		return getRandomNames(NUMBER_OF_NAMES);
	}
	
	public static ArrayList<String> getRandomNames(int number) {
		ArrayList<String> names = new ArrayList<String>();
		Random randomGenerator = new Random();
		for (int idx = 1; idx <= number; ++idx){
			int randomInt = randomGenerator.nextInt(FIRST_NAMES_ALL_LIST.size());
			String firstName = FIRST_NAMES_ALL_LIST.get(randomInt);
			randomInt = randomGenerator.nextInt(LAST_NAMES_LIST.size());
			String lastName = LAST_NAMES_LIST.get(randomInt);
			String name = firstName + " " + lastName;
			names.add(name);
		}
		return names;
	}

}
