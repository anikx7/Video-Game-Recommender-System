package com.example.myapplication;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SimpleTimeZone;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.os.Environment;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class Main4Activity extends Activity {

    //Intent message from activity 3
    String message1;

    InputStream inputStream;
    InputStream inputStream2;
    String[] data;
    String[] data2;
    Vector<Vector <String>> tokenized_review = new Vector<>();

    Vector<String>data1_line = new Vector<>();
    Vector<String>data2_line = new Vector<>();
    Vector<String> unique_class = new Vector<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        Intent in=getIntent();
        message1 = in.getStringExtra("userName");



        //Reading data from data.csv (for user reviews)
        inputStream2 = getResources().openRawResource(R.raw.data2);
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(inputStream2));
        try {
            String csvLine2;
            while ((csvLine2 = reader2.readLine()) != null)
            {
                data2_line.add(csvLine2);
                data2=csvLine2.split(",");
                try{

                }catch (Exception e){
                    Log.e("Problem",e.toString());
                }
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }

        //Reading from file genre_data.csv
        inputStream = getResources().openRawResource(R.raw.genre_data3);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                data1_line.add(csvLine);
                data=csvLine.split(",");
                try{


                }catch (Exception e){
                    Log.e("Problem",e.toString());
                }
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }


        int index = 0;
        String [] temp1;
        String [] temp3;

        Vector<String>genre = new Vector<>();
        Vector<String>game_number2 = new Vector<>();
        Vector<String>title_data2 = new Vector<>();
        Vector<String>platform2 = new Vector<>();
        Vector<String>userscore2 = new Vector<>();
        Vector<String>review2 = new Vector<>();
        Vector<String>genre2 = new Vector<>();

        Vector<String>bag_of_words = new Vector<>();
        Vector<Vector <String>> tokenized_review = new Vector<>(); // to store tokens

        Vector<Integer> action_index= new Vector<>();
        Vector<Integer> sports_index= new Vector<>();
        Vector<Integer> driving_index= new Vector<>();
        Vector<Integer> puzzle_index= new Vector<>();
        Vector<Integer> strategy_index= new Vector<>();
        Vector<Integer> adventure_index= new Vector<>();
        Vector<Integer> racing_index= new Vector<>();
        Vector<Integer> fighting_index= new Vector<>();
        Vector<Integer> shooter_index= new Vector<>();
        Vector<Integer> compilation_index= new Vector<>();
        Vector<Integer> miscella_index= new Vector<>();



        //Separating data for genre
        //Adding unique classes
        unique_class.add("action");
        unique_class.add("sports");
        unique_class.add("driving");
        unique_class.add("puzzle");
        unique_class.add("strategy");
        unique_class.add("adventure");
        unique_class.add("racing");
        unique_class.add("fighting");
        unique_class.add("shooter");
        unique_class.add("compilation");
        unique_class.add("miscellaneous");
        double action_count=0,sports_count=0,driving_count=0,puzzle_count=0,strategy_count=0,adventure_count=0,
                racing_count=0,fighting_count=0,shooter_count=0,
                compilation_count=0, miscella_count = 0;
        String genre_temp;
        while (index < data1_line.size())
        {
            temp1=data1_line.get(index).split(",");
            genre.add(temp1[3].toLowerCase());
            genre_temp = temp1[3].toLowerCase();

            //Counting class
            if(genre_temp.equalsIgnoreCase("action")) {
                action_count++;//counting
                action_index.add(index);//storing indexes
            }
            else if (genre_temp.equalsIgnoreCase("sports")) {
                sports_count++;
                sports_index.add(index);
            }
            else if(genre_temp.equalsIgnoreCase("driving")) {
                driving_count++;
                driving_index.add(index);
            }
            else if(genre_temp.equalsIgnoreCase("puzzle")) {
                puzzle_count++;
                puzzle_index.add(index);
            }
            else if(genre_temp.equalsIgnoreCase("strategy")) {
                strategy_count++;
            }
            else if(genre_temp.equalsIgnoreCase("adventure")) {
                adventure_count++;
                adventure_index.add(index);
            }
            else if(genre_temp.equalsIgnoreCase("racing")) {
                racing_count++;
                racing_index.add(index);
            }
            else if(genre_temp.equalsIgnoreCase("fighting")) {
                fighting_count++;
                fighting_index.add(index);
            }
            else if(genre_temp.equalsIgnoreCase("shooter")) {
                shooter_count++;
                shooter_index.add(index);
            }
            else if(genre_temp.equalsIgnoreCase("compilation")) {
                compilation_count++;
                compilation_index.add(index);
            }
            else if(genre_temp.equalsIgnoreCase("miscellaneous")) {
                miscella_count++;
                miscella_index.add(index);
            }
            index++;
        }



        //Separating data
        index=1; // omitting first column

        while (index < data2_line.size()) {
            temp3=data2_line.get(index).split(",");
            game_number2.add(temp3[0]);
            title_data2.add(temp3[1]);
            platform2.add(temp3[2]);
            userscore2.add(temp3[3]);
            genre2.add(" ");
            //review2.add(temp3[4]);

            //Filtering data: deleting stopwords
            //Source: https://stackoverflow.com/questions/27685839/removing-stopwords-from-a-string-in-java
            Pattern p = Pattern.compile("\\b(i|me|my|myself|we|our|ours|ourselves|you|your|yours|yourselves|he|him|she|her|himself|herself|his|" +
                    "it|its|it's|itself|they|them|their|theirs|themselves|what|which|who|whom|this|that" +
                    "|these|those|am|is|are|was|were|be|been|being|have|has|had|having|do|does|did|a|an|the|and" +
                    "|but|if|or|because|as|until|while|of|at|by|for|with|about|against|between|into|through|during|" +
                    "before|after|above|below|to|from|up|down|in|out|on|off|over|under|again|further|then|once|here|" +
                    "there|when|where|why|how|all|any|both|each|few|more|most|other|some|such|no|nor|not|only|own|same" +
                    "|so|than|too|very|can|will|just|don|should|now|;|\"|)\\b\\s?");
            Matcher m = p.matcher(temp3[4].toLowerCase()); // converting everything into lowercase
            String s = m.replaceAll(" ");


            //Tokenizing
            StringTokenizer st1 = new StringTokenizer(s.toLowerCase());
            Vector<String> tokenized = new Vector<>();
            tokenized.add(genre.get(index-1));
            tokenized.add(temp3[1]);
            while (st1.hasMoreTokens()) {
                String temp_token = st1.nextToken();
                if (temp_token.equals("\"") || temp_token.equals(" \"")||temp_token.equals(";")||temp_token.equals("!")||temp_token.equals(" 've")
                        ||temp_token.equals("s")||temp_token.equals("-")||temp_token.equals("ve")||temp_token.equals("'")||temp_token.equals(".")) {
                }
                else {
                    tokenized.add(temp_token);
                    bag_of_words.add(temp_token);
                }
            }

            tokenized_review.add(tokenized);
            review2.add(s);
            index++;
        }

        HashSet<String> unique_bag_of_words = new HashSet<String>(bag_of_words); // removing duplicates from the bag of words



        //Finding probabilty of each class
        double p_action= action_count/genre2.size();
        double p_sports=sports_count/genre2.size();
        double p_driving=driving_count/genre2.size();
        double p_puzzle=puzzle_count/genre2.size();
        double p_strategy=strategy_count/genre2.size();
        double p_adventure=adventure_count/genre2.size();
        double p_racing=racing_count/genre2.size();
        double p_fighting=fighting_count/genre2.size();
        double p_shooter=shooter_count/genre2.size();
        double p_compilation=compilation_count/genre2.size();
        double p_miscella = miscella_count/genre2.size();


        //User Query
        Vector<String> user_query = new Vector<>();
        //Counting words
        String trim = message1.trim();
        int user_query_length =  trim.split("\\s+").length;

        //Tokenizing user query
        StringTokenizer st = new StringTokenizer(message1.toLowerCase());
        while (st.hasMoreTokens()) {
            String temp_token = st.nextToken();
            if(temp_token.equalsIgnoreCase( "games" )|| temp_token.equalsIgnoreCase( "game's"))
                temp_token= "game";
            if(temp_token.equalsIgnoreCase( "fun" )|| temp_token.equalsIgnoreCase( "funniest"))
                temp_token= "funny";
            if(temp_token.equalsIgnoreCase( "shooter" )|| temp_token.equalsIgnoreCase( "shooting"))
                temp_token= "shooter";
            user_query.add(temp_token);
        }


        Vector<Double> probabilties = new Vector<>();

        //Finding probability of action class
        index = 0;
        String user_query_term = " ";
        double user_p_action=0.0;
        double cal_p =0.0;
        int index2=0;
        int index3=0;
        int word_count=0;
        Vector<String> token_doc ;
        while (index< user_query.size())
        {
            user_query_term = user_query.get(index);
            index2 = 0;
            word_count = 0;
            while (index2<action_index.size())
            {
                token_doc = tokenized_review.get(action_index.get(index2));
                index3 = 0;
                while (index3<token_doc.size())
                {
                    if (user_query_term.equalsIgnoreCase(token_doc.get(index3)))
                        word_count++;
                    index3++;
                }
                index2++;
            }
            cal_p = (word_count+1)/(action_count+unique_bag_of_words.size());
            user_p_action = user_p_action + cal_p;
            cal_p=0.0;

            index++;
        }
        probabilties.add(user_p_action);



        //Finding probability of Sports class
        index = 0;
        user_query_term = " ";
        double user_p_sports=0.0;
        cal_p =0.0;
        index2=0;
        index3=0;
        word_count=0;

        while (index< user_query.size())
        {
            user_query_term = user_query.get(index);
            index2 = 0;
            word_count = 0;
            while (index2<sports_index.size())
            {
                token_doc = tokenized_review.get(sports_index.get(index2));
                index3 = 0;
                while (index3<token_doc.size())
                {
                    if (user_query_term.equalsIgnoreCase(token_doc.get(index3)))
                        word_count++;
                    index3++;
                }
                index2++;
            }
            cal_p = (word_count+1)/(sports_count+unique_bag_of_words.size());
            user_p_sports = user_p_sports + cal_p;
            cal_p=0.0;
            index++;
        }
        probabilties.add(user_p_sports);

        //Finding probability of driving class
        index = 0;
        user_query_term = " ";
        double user_p_driving=0.0;
        cal_p =0.0;
        index2=0;
        index3=0;
        word_count=0;

        while (index< user_query.size())
        {
            user_query_term = user_query.get(index);
            index2 = 0;
            word_count = 0;
            while (index2<driving_index.size())
            {
                token_doc = tokenized_review.get(driving_index.get(index2));
                index3 = 0;
                while (index3<token_doc.size())
                {
                    if (user_query_term.equalsIgnoreCase(token_doc.get(index3)))
                        word_count++;
                    index3++;
                }
                index2++;
            }
            cal_p = (word_count+1)/(driving_count+unique_bag_of_words.size());
            user_p_driving = user_p_driving + cal_p;
            cal_p=0.0;
            index++;
        }
        probabilties.add(user_p_driving);



        //Finding probability of puzzle class
        index = 0;
        user_query_term = " ";
        double user_p_puzzle=0.0;
        cal_p =0.0;
        index2=0;
        index3=0;
        word_count=0;

        while (index< user_query.size())
        {
            user_query_term = user_query.get(index);
            index2 = 0;
            word_count = 0;
            while (index2<puzzle_index.size())
            {
                token_doc = tokenized_review.get(puzzle_index.get(index2));
                index3 = 0;
                while (index3<token_doc.size())
                {
                    if (user_query_term.equalsIgnoreCase(token_doc.get(index3)))
                        word_count++;
                    index3++;
                }
                index2++;
            }
            cal_p = (word_count+1)/(puzzle_count+unique_bag_of_words.size());
            user_p_puzzle = user_p_puzzle + cal_p;
            cal_p=0.0;
            index++;
        }
        probabilties.add(user_p_puzzle);




        //Finding probability of strategy class
        index = 0;
        user_query_term = " ";
        double user_p_strategy=0.0;
        cal_p =0.0;
        index2=0;
        index3=0;
        word_count=0;

        while (index< user_query.size())
        {
            user_query_term = user_query.get(index);
            index2 = 0;
            word_count = 0;
            while (index2<strategy_index.size())
            {
                token_doc = tokenized_review.get(strategy_index.get(index2));
                index3 = 0;
                while (index3<token_doc.size())
                {
                    if (user_query_term.equalsIgnoreCase(token_doc.get(index3)))
                        word_count++;
                    index3++;
                }
                index2++;
            }
            cal_p = (word_count+1)/(strategy_count+unique_bag_of_words.size());
            user_p_strategy = user_p_strategy + cal_p;
            cal_p=0.0;
            index++;
        }

        probabilties.add(user_p_strategy);


        //Finding probability of adventure class
        index = 0;
        user_query_term = " ";
        double user_p_adventure=0.0;
        cal_p =0.0;
        index2=0;
        index3=0;
        word_count=0;

        while (index< user_query.size())
        {
            user_query_term = user_query.get(index);
            index2 = 0;
            word_count = 0;
            while (index2<adventure_index.size())
            {
                token_doc = tokenized_review.get(adventure_index.get(index2));
                index3 = 0;
                while (index3<token_doc.size())
                {
                    if (user_query_term.equalsIgnoreCase(token_doc.get(index3)))
                        word_count++;
                    index3++;
                }
                index2++;
            }
            cal_p = (word_count+1)/(adventure_count+unique_bag_of_words.size());
            user_p_adventure = user_p_adventure + cal_p;
            cal_p=0.0;
            index++;
        }
        probabilties.add(user_p_adventure);


        //Finding probability of racing class
        index = 0;
        user_query_term = " ";
        double user_p_racing=0.0;
        cal_p =0.0;
        index2=0;
        index3=0;
        word_count=0;

        while (index< user_query.size())
        {
            user_query_term = user_query.get(index);
            index2 = 0;
            word_count = 0;
            while (index2<racing_index.size())
            {
                token_doc = tokenized_review.get(racing_index.get(index2));
                index3 = 0;
                while (index3<token_doc.size())
                {
                    if (user_query_term.equalsIgnoreCase(token_doc.get(index3)))
                        word_count++;
                    index3++;
                }
                index2++;
            }
            cal_p = (word_count+1)/(racing_count+unique_bag_of_words.size());
            user_p_racing = user_p_racing + cal_p;
            cal_p=0.0;
            index++;
        }
        probabilties.add(user_p_racing);


        //Finding probability of fighting class
        index = 0;
        user_query_term = " ";
        double user_p_fighting=0.0;
        cal_p =0.0;
        index2=0;
        index3=0;
        word_count=0;

        while (index< user_query.size())
        {
            user_query_term = user_query.get(index);
            index2 = 0;
            word_count = 0;
            while (index2<fighting_index.size())
            {
                token_doc = tokenized_review.get(fighting_index.get(index2));
                index3 = 0;
                while (index3<token_doc.size())
                {
                    if (user_query_term.equalsIgnoreCase(token_doc.get(index3)))
                        word_count++;
                    index3++;
                }
                index2++;
            }
            cal_p = (word_count+1)/(fighting_count+unique_bag_of_words.size());
            user_p_fighting = user_p_fighting + cal_p;
            cal_p=0.0;
            index++;
        }
        probabilties.add(user_p_fighting);



        //Finding probability of shooter class
        index = 0;
        user_query_term = " ";
        double user_p_shooter=0.0;
        cal_p =0.0;
        index2=0;
        index3=0;
        word_count=0;

        while (index< user_query.size())
        {
            user_query_term = user_query.get(index);
            index2 = 0;
            word_count = 0;
            while (index2<shooter_index.size())
            {
                token_doc = tokenized_review.get(shooter_index.get(index2));
                index3 = 0;
                while (index3<token_doc.size())
                {
                    if (user_query_term.equalsIgnoreCase(token_doc.get(index3)))
                        word_count++;
                    index3++;
                }
                index2++;
            }
            cal_p = (word_count+1)/(shooter_count+unique_bag_of_words.size());
            user_p_shooter = user_p_shooter + cal_p;
            cal_p=0.0;
            index++;
        }
        probabilties.add(user_p_shooter);


        //Finding probability of compilation class
        index = 0;
        user_query_term = " ";
        double user_p_compilation=0.0;
        cal_p =0.0;
        index2=0;
        index3=0;
        word_count=0;

        while (index< user_query.size())
        {
            user_query_term = user_query.get(index);
            index2 = 0;
            word_count = 0;
            while (index2<compilation_index.size())
            {
                token_doc = tokenized_review.get(compilation_index.get(index2));
                index3 = 0;
                while (index3<token_doc.size())
                {
                    if (user_query_term.equalsIgnoreCase(token_doc.get(index3)))
                        word_count++;
                    index3++;
                }
                index2++;
            }
            cal_p = (word_count+1)/(compilation_count+unique_bag_of_words.size());
            user_p_compilation = user_p_compilation + cal_p;
            cal_p=0.0;
            index++;
        }
        probabilties.add(user_p_compilation);


        //Finding probability of miscella class
        index = 0;
        user_query_term = " ";
        double user_p_miscella=0.0;
        cal_p =0.0;
        index2=0;
        index3=0;
        word_count=0;

        while (index< user_query.size())
        {
            user_query_term = user_query.get(index);
            index2 = 0;
            word_count = 0;
            while (index2<miscella_index.size())
            {
                token_doc = tokenized_review.get(miscella_index.get(index2));
                index3 = 0;
                while (index3<token_doc.size())
                {
                    if (user_query_term.equalsIgnoreCase(token_doc.get(index3)))
                        word_count++;
                    index3++;
                }
                index2++;
            }
            cal_p = (word_count+1)/(miscella_count+unique_bag_of_words.size());
            user_p_miscella = user_p_miscella + cal_p;
            cal_p=0.0;
            index++;
        }
        probabilties.add(user_p_miscella);

        Collections.sort(probabilties);







        //Displaying output in the screen
        Vector<String>list_view_output = new Vector<>();
        ArrayList<String> list_item;
        list_item = new ArrayList<>();
        index=1;
        double prob_check = 0.0;
        String temp2 =" Top 3 most likely genre for User Query: "+message1+"\n\n";

        while (index<4)
        {
            prob_check = probabilties.get(probabilties.size()-(index));
            if (prob_check == user_p_action)
            {
                temp2 = temp2 + Integer.toString(index)+". Action\n";
            }
            if (prob_check == user_p_sports)
            {
                temp2 = temp2 + Integer.toString(index)+". Sports\n";
            }
            if (prob_check == user_p_driving)
            {
                temp2 = temp2 + Integer.toString(index)+". Driving\n";
            }
            if (prob_check == user_p_puzzle)
            {
                temp2 = temp2 + Integer.toString(index)+". Puzzle\n";
            }
            if (prob_check == user_p_strategy)
            {
                temp2 = temp2 + Integer.toString(index)+". Strategy\n";
            }
            if (prob_check == user_p_adventure)
            {
                temp2 = temp2 + Integer.toString(index)+". Adventure\n";
            }
            if (prob_check == user_p_racing)
            {
                temp2 = temp2 + Integer.toString(index)+". Racing\n";
            }
            if (prob_check == user_p_fighting)
            {
                temp2 = temp2 + Integer.toString(index)+". Fighting\n";
            }
            if (prob_check == user_p_shooter)
            {
                temp2 = temp2 + Integer.toString(index)+". Shooter\n";
            }
            if (prob_check == user_p_compilation)
            {
                temp2 = temp2 + Integer.toString(index)+". Compilaton\n";
            }
            if (prob_check == user_p_miscella)
            {
                temp2 = temp2 + Integer.toString(index)+". Miscellaneous\n";
            }
            index++;
        }

        //Calculation showing

        temp2 = temp2 + "\n\n\n\n\n Calculations\n";
        temp2 = temp2 + "\nAll class probabilties\n";
        temp2 = temp2 + "P(Action) = "+Double.toString(p_action)+"\n";
        temp2 = temp2 + "P(Sports) = "+Double.toString(p_sports)+"\n";
        temp2 = temp2 + "P(Driving) = "+Double.toString(p_driving)+"\n";
        temp2 = temp2 + "P(Puzzle) = "+Double.toString(p_puzzle)+"\n";
        temp2 = temp2 + "P(Strategy) = "+Double.toString(p_strategy)+"\n";
        temp2 = temp2 + "P(Adventure) = "+Double.toString(p_adventure)+"\n";
        temp2 = temp2 + "P(Racing) = "+Double.toString(p_racing)+"\n";
        temp2 = temp2 + "P(Fighting) = "+Double.toString(p_fighting)+"\n";
        temp2 = temp2 + "P(Shooter) = "+Double.toString(p_shooter)+"\n";
        temp2 = temp2 + "P(Compilation) = "+Double.toString(p_compilation)+"\n";
        temp2 = temp2 + "P(Miscellaneous) = "+Double.toString(p_miscella)+"\n";


        temp2 = temp2 + "\nChoosing a class\n";
        temp2 = temp2 + "P(Action | Query) = "+Double.toString(user_p_action)+"\n";
        temp2 = temp2 + "P(Sports | Query) = "+Double.toString(user_p_sports)+"\n";
        temp2 = temp2 + "P(Driving | Query)="+Double.toString(user_p_driving)+"\n";
        temp2 = temp2 + "P(Puzzle | Query) = "+Double.toString(user_p_puzzle)+"\n";
        temp2 = temp2 + "P(Strategy | Query) = "+Double.toString(user_p_strategy)+"\n";
        temp2 = temp2 + "P(Adventure | Query) = "+Double.toString(user_p_adventure)+"\n";
        temp2 = temp2 + "P(Racing | Query) = "+Double.toString(user_p_racing)+"\n";
        temp2 = temp2 + "P(Fighting | Query) ="+Double.toString(user_p_fighting)+"\n";
        temp2 = temp2 + "P(Shooter | Query) = "+Double.toString(user_p_shooter)+"\n";
        temp2 = temp2 + "P(Compilation | Query) = "+Double.toString(user_p_compilation)+"\n";
        temp2 = temp2 + "P(Miscellaneous | Query) = "+Double.toString(user_p_miscella)+"\n";




        list_view_output.add(temp2);
        list_item.add(temp2);
        ListAdapter theAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list_item);
        ListView theListView = (ListView) findViewById(R.id.theListView);
        theListView.setAdapter(theAdapter);

    }

}

