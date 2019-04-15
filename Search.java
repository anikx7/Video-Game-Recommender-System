package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.os.Environment;



public class Main2Activity extends Activity {


    //Intent message from activity 1
    String message1;

    InputStream inputStream;
    InputStream inputStream2;
    String[] data;
    String[] data2;
    Vector<Vector <String>> tokenized_review = new Vector<>();

    Vector<String>data1_line = new Vector<>();
    Vector<String>data2_line = new Vector<>();
    Vector<Integer> top10 = new Vector<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent in=getIntent();
        message1 = in.getStringExtra("userName");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        //Reading data from data2.csv (for user reviews)
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


        int index = 1;
        String [] temp1;
        String [] temp3;


        Vector<String>bag_of_words = new Vector<>();

        Vector<String>game_number2 = new Vector<>();
        Vector<String>title_data2 = new Vector<>();
        Vector<String>platform2 = new Vector<>();
        Vector<String>userscore2 = new Vector<>();
        Vector<String>review2 = new Vector<>();
        Vector<String>review_genre = new Vector<>();
        Vector<String>review_without_filter = new Vector<>();

        Vector<Map<String, Double>> tf_idf_of_token = new Vector<>();
        Vector<Vector>tokenized_review_tf_idf = new Vector<>();
        Vector<Vector>tf_idf = new Vector<>();
        Vector<Double>cosine_similarities = new Vector<>();
        Vector<Integer>cosine_similarities_index_temp = new Vector<>();
        Vector<Integer>cosine_similarities_index_final = new Vector<>();




        //Separating datacl
        index=1; // omitting first column

        while (index < data2_line.size()) {
            temp3=data2_line.get(index).split(",");
            game_number2.add(temp3[0]);
            title_data2.add(temp3[1]);
            platform2.add(temp3[2]);
            userscore2.add(temp3[3]);
            review2.add(temp3[4]);
            index++;
        }


        //Merging two file and text reviews
        index = 0;
        int index2=0;
        String merge;
        while (index < title_data2.size()) {
            //Source: https://stackoverflow.com/questions/27685839/removing-stopwords-from-a-string-in-java
            Pattern p = Pattern.compile("\\b(i|me|my|myself|we|our|ours|ourselves|you|your|yours|yourselves|he|him|she|her|himself|herself|his|" +
                    "it|its|it's|itself|they|them|their|theirs|themselves|what|which|who|whom|this|that" +
                    "|these|those|am|is|are|was|were|be|been|being|have|has|had|having|do|does|did|a|an|the|and" +
                    "|but|if|or|because|as|until|while|of|at|by|for|with|about|against|between|into|through|during|" +
                    "before|after|above|below|to|from|up|down|in|out|on|off|over|under|again|further|then|once|here|" +
                    "there|when|where|why|how|all|any|both|each|few|more|most|other|some|such|no|nor|not|only|own|same" +
                    "|so|than|too|very|can|will|just|don|should|now|;|\"|)\\b\\s?");
            Matcher m = p.matcher(review2.get(index).toLowerCase()); // converting everything into lowercase
            String s = m.replaceAll(" ");

            //Tokenizing
            StringTokenizer st1 = new StringTokenizer(s.toLowerCase());
            Vector<String> tokenized = new Vector<>();
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

            index++;
        }



        //IDF value calculated and saving
        /*index =0;
        index2 =0;
        Vector<Double> weight = new Vector<>();
        String weight_sum ="";
        while (index <bag_of_words.size())
        {
            weight.add(idf(bag_of_words.get(index)));
            Log.d("Hakula5th:",Double.toString(weight.get(index2)));

            index++;
            index2++;

        }
        Log.d("Hakula5th:",Integer.toString(weight.size()));
*/

        //Reading previously calculated idf values
        inputStream = getResources().openRawResource(R.raw.token_tf_idf);
        BufferedReader reader3 = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine3;
            while ((csvLine3 = reader3.readLine()) != null)
            {
                data1_line.add(csvLine3);
                data=csvLine3.split(",");

            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }
        index=0;
        Vector<Double> idf = new Vector<>();
        while (index < data1_line.size()) {
            temp3=data1_line.get(index).split(",");
            idf.add(Double.parseDouble(temp3[1]));
            index++;
        }





        index =0;
        int index3=0;
        while (index<title_data2.size()) {
            Double weight_cal = 0.0;
            Map<String, Double> map = new HashMap<String, Double>();
            Vector<String> temp_tokenized_review = tokenized_review.get(index);
            index2 = 0;
            while (index2 < temp_tokenized_review.size()) {
                weight_cal = (1+Math.log10(tf(temp_tokenized_review, temp_tokenized_review.get(index2))))*(Math.log10(idf.get(index3)));
                //weight_cal = (tf(temp_tokenized_review, temp_tokenized_review.get(index2)));

                map.put(temp_tokenized_review.get(index2), weight_cal);
                index2++;
                index3++;
            }
            tf_idf_of_token.add(map);
            index++;
        }



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

        //Calculating weight for user query
        index=0;
        Vector<Double> user_query_weight = new Vector<>();
        while (index<user_query.size()){
            user_query_weight.add(1+Math.log10(tf(user_query, user_query.get(index))));//Calculating weight
            Log.d("Anik",Double.toString(user_query_weight.get(0)));
            index++;
        }

        Vector<Integer> posting_list_1st_index = new Vector<>();
        Vector<Double> posting_list_1st_weight = new Vector<>();
        Vector<Integer> posting_list_2nd_index = new Vector<>();
        Vector<Double> posting_list_2nd_weight = new Vector<>();
        Vector<Integer> posting_list_3rd_index = new Vector<>();
        Vector<Double> posting_list_3rd_weight = new Vector<>();
        Vector<Integer> posting_list_4th_index = new Vector<>();
        Vector<Double> posting_list_4th_weight = new Vector<>();
        Vector<Integer> posting_list_5th_index = new Vector<>();
        Vector<Double> posting_list_5th_weight = new Vector<>();
        Vector<Integer> posting_list_6th_index = new Vector<>();
        Vector<Double> posting_list_6th_weight = new Vector<>();

        Vector<Double>weight_temp = new Vector<>();
        Vector<Integer>index_weight = new Vector<>();
        //Posting list construction (7.1)
        index = 0;
        while (index<user_query.size())
        {
            String user_query_term = user_query.get(index);
            index2=0;
            Map<Integer, Double> map = new HashMap<Integer, Double>();
            Map<Double, Integer> map_sorting = new HashMap< Double,Integer>();
            while (index2<tokenized_review.size())
            {
                Vector<String>tokens = tokenized_review.get(index2);
                index3=0;

                while (index3<tokens.size())
                {
                    if(tokens.get(index3).equals(user_query_term)) {
                        Map<String, Double> retreive_weight = tf_idf_of_token.get(index2);//For retrival
                        weight_temp.add(retreive_weight.get(user_query_term));
                        //index_weight.add(index2);
                        map_sorting.put(retreive_weight.get(user_query_term),index2);
                        //map.put(index2,retreive_weight.get(user_query_term));//Getting weight & saving into map
                        break;
                    }
                    index3++;
                }
                index2++;
            }
            if (index == 0) {
                Collections.sort(weight_temp);//Sorting ascending
                Collections.reverse(weight_temp); //Desceding order
                int index4=0;
                while (index4<weight_temp.size())
                {
                    index_weight.add(map_sorting.get(weight_temp.get(index4)));
                    //map.put(map_sorting.get(weight_temp.get(index4)),weight_temp.get(index4));//Getting weight & saving into map
                    index4++;
                }

                posting_list_1st_weight = (Vector<Double>) weight_temp.clone();
                Vector newVect = (Vector<Integer>) index_weight.clone(); // For removing duplicates
                posting_list_1st_index = new Vector(new LinkedHashSet(newVect));
                index_weight.clear();
                weight_temp.clear();
                map_sorting.clear();

            }
            else if (index ==1) {
                Collections.sort(weight_temp);//Sorting ascending
                Collections.reverse(weight_temp); //Desceding order
                int index4=0;
                while (index4<weight_temp.size())
                {
                    index_weight.add(map_sorting.get(weight_temp.get(index4)));
                    //map.put(map_sorting.get(weight_temp.get(index4)),weight_temp.get(index4));//Getting weight & saving into map
                    index4++;
                }
                Vector newVect = (Vector<Integer>) index_weight.clone(); // For removing duplicates
                posting_list_2nd_index = new Vector(new LinkedHashSet(newVect));
                posting_list_2nd_weight = (Vector<Double>) weight_temp.clone();
                index_weight.clear();
                weight_temp.clear();
                map_sorting.clear();
            }
            else if (index ==2){
                Collections.sort(weight_temp);//Sorting ascending
                Collections.reverse(weight_temp); //Desceding order
                int index4=0;
                while (index4<weight_temp.size())
                {
                    index_weight.add(map_sorting.get(weight_temp.get(index4)));
                    //map.put(map_sorting.get(weight_temp.get(index4)),weight_temp.get(index4));//Getting weight & saving into map
                    index4++;
                }
                Vector newVect = (Vector<Integer>) index_weight.clone(); // For removing duplicates
                posting_list_3rd_index = new Vector(new LinkedHashSet(newVect));
                posting_list_3rd_weight = weight_temp;
                index_weight.clear();
                weight_temp.clear();
                map_sorting.clear();
            }
            map.clear();
            index++;
        }




        //If user query term is 1 and match found
        index = 0;
        if(user_query.size() == 1) {
            Map<Double,Integer> map = new HashMap<Double,Integer>();
            Double cosine_sim_calc = 0.0;
            while (index < 10 && index< posting_list_1st_weight.size()) {
                cosine_sim_calc = posting_list_1st_weight.get(index)*user_query_weight.get(0);
                cosine_similarities.add(cosine_sim_calc);
                map.put(cosine_sim_calc,posting_list_1st_index.get(index));
                //cosine_similarities_index_temp.add(posting_list_1st_index.get(index));
                index++;
            }
            Collections.sort(cosine_similarities);//Sorting ascending
            Collections.reverse(cosine_similarities); //Desceding order
            int index4=0;
            while (index4<cosine_similarities.size()) // Similarities descending order
            {
                cosine_similarities_index_final.add(map.get(cosine_similarities.get(index4)));
                top10.add(map.get(cosine_similarities.get(index4)));
                index4++;
            }

        }//


        //Returned 10 posting list
        Vector<Integer> exist_in_all_token = new Vector<>();
        index = 0;
        if (user_query.size() == 2 )
        {
            double cosine_sim =0.0;
            index = 0;
            Map<Double,Integer> map = new HashMap<Double,Integer>();

            while (index <posting_list_1st_index.size())
            {
                index2 = 0;
                while (index2<posting_list_2nd_index.size())
                {
                    int x = posting_list_1st_index.get(index);
                    int y = posting_list_2nd_index.get(index2);
                    if(x == y)
                    {
                        exist_in_all_token.add(x);//finding token exist in all tokens
                        int index6 = 0;
                        while (index6 < user_query.size())
                        {
                            if (index6==0)
                                cosine_sim = cosine_sim + (user_query_weight.get(index6)*posting_list_1st_weight.get(index));
                            else
                                cosine_sim = cosine_sim + (user_query_weight.get(index6)*posting_list_2nd_weight.get(index));
                            index6++;
                        }
                        map.put(cosine_sim,posting_list_1st_index.get(index));
                        cosine_similarities.add(cosine_sim);
                        cosine_sim =0.0;

                    }
                    index2++;

                }
                index++;
            }
            Collections.sort(cosine_similarities);//Sorting ascending
            Collections.reverse(cosine_similarities); //Desceding order
            int index4=0;
            while (index4<cosine_similarities.size()) // Similarities descending order
            {
                cosine_similarities_index_final.add(map.get(cosine_similarities.get(index4)));
                top10.add(map.get(cosine_similarities.get(index4)));
                index4++;
            }
            if(top10.size()<10)
            {
                //Upper bound calculation
                int index6 = 0;
                Double upper_bound = 0.0;
                while (index6 <user_query.size())
                {
                    if (index6 == 0)
                        upper_bound = upper_bound + (user_query_weight.get(index6)*posting_list_1st_weight.get(10));//10th element upper bound
                    if (index6 == 1)
                        upper_bound = upper_bound + (user_query_weight.get(index6)*posting_list_2nd_weight.get(10));//10th element upper bound

                    index6++;

                }



                int index8=0;
                while (index8 < posting_list_1st_index.size())
                {
                    double sim = 0.0;
                    int index9=0;
                    while (index9<user_query.size()) {
                        if(index9 == 0)
                            sim = sim+posting_list_1st_weight.get(index8) * user_query_weight.get(index9);
                        else
                            sim = sim+ posting_list_2nd_weight.get(10)*user_query_weight.get(index9);
                        index9++;
                    }
                    if((sim > upper_bound)&& top10.size()<10)
                        top10.add(posting_list_1st_index.get(index8));
                    index8++;

                }
                index8=0;
                while (index8 < posting_list_2nd_index.size())
                {
                    double sim = 0.0;
                    int index9=0;
                    while (index9<user_query.size()) {
                        if(index9 == 0)
                            sim = sim+posting_list_1st_weight.get(10) * user_query_weight.get(index9);
                        else
                            sim = sim+ posting_list_2nd_weight.get(index8)*user_query_weight.get(index9);
                        index9++;
                    }
                    if((sim > upper_bound)&& top10.size()<10)
                        top10.add(posting_list_1st_index.get(index8));
                    index8++;

                }


                Log.d("fg",Double.toString(posting_list_1st_weight.get(10)));


            }

            //Similarities calculation

        }

        if (user_query.size() == 3 )
        {
            double cosine_sim =0.0;
            index = 0;
            Map<Double,Integer> map = new HashMap<Double,Integer>();

            while (index <posting_list_1st_index.size())
            {
                index2 = 0;
                while (index2<posting_list_2nd_index.size())
                {
                    int x = posting_list_1st_index.get(index);
                    int y = posting_list_2nd_index.get(index2);
                    index3 = 0;
                    while (index3<posting_list_3rd_index.size())
                    {
                        int z = posting_list_3rd_index.get(index3);
                        if(x == y && x == z & z == y)
                        {
                            exist_in_all_token.add(x);//finding token exist in all tokens
                            top10.add(x);

                        }
                        index3++;
                    }

                    index2++;

                }
                index++;
            }
            index=0;
            while (index<posting_list_1st_index.size() && top10.size()<10)
            {
                top10.add(posting_list_1st_index.get(index));
                index++;
            }
            index=0;
            while (index<posting_list_2nd_index.size() && top10.size()<10)
            {
                top10.add(posting_list_2nd_index.get(index));
                index++;
            }
            index=0;
            while (index<posting_list_3rd_index.size() && top10.size()<10)
            {
                top10.add(posting_list_3rd_index.get(index));
                index++;
            }

        }


        String temp2;
        index=0;
        int game_match_counter = 0;
        Vector<String>list_view_output = new Vector<>();
        ArrayList<String> list_item;
        list_item = new ArrayList<>();


        //Displaying output in the screen
        while (index < 10 && index < top10.size())
        {
            if (top10.size() == 0) {
                temp2 = "No document found!!!";
                break;
            }
            else
                temp2 = Integer.toString(index+1)+". Title: "+title_data2.get(top10.get(index))+"\n User Comment: "+review2.get(top10.get(index))+"\n\n";
            list_view_output.add(temp2);
            list_item.add(temp2);
            index++;
        }


        ListAdapter theAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list_item);
        ListView theListView = (ListView) findViewById(R.id.theListView);
        theListView.setAdapter(theAdapter);



        tf_idf.clear();
        cosine_similarities.clear();


    }

    //Calculating tf score
    public double tf(Vector<String> doc1, String user_query_term) {
        Double result = 0.0;
        int index = 0;
        while (index <doc1.size())
        {
            if (user_query_term.equalsIgnoreCase(doc1.get(index)))
                result++;
            index++;
        }
        if (result == 0)
            return 0.0;
        return (result/doc1.size()) ;
    }

    //Finding idf value
    public double idf(String term) {
        double count = 0;
        int index = 0;
        while (index<tokenized_review.size())
        {
            Vector<String> temp_query = tokenized_review.get(index);
            int index3=0;
            while (index3 < temp_query.size()){
                if (term.equalsIgnoreCase(temp_query.get(index3))) {
                    count++;
                    break;
                }
                index3++;
            }
            index++;
        }
        if (count == 0)
            return 1.0;
        return ((tokenized_review.size()) / count);
    }

    //Source: https://github.com/AdnanOquaish/Cosine-similarity-Tf-Idf-/blob/master/DocumentParser.java
    public double cos_sim(Vector<Double>Vector1, Vector<Double> Vector2) {
        double mul = 0.0,sum1 = 0.0,sum2=0.0,cos_sim_value = 0.0;

        //Converting Vector to array for cosine similarity
        double vector1[] = new double[Vector1.size()];
        for (int i=0; i<Vector1.size(); i++) {
            vector1[i] = Vector1.get(i);
        }
        double vector2[] = new double[Vector2.size()];
        for (int i=0; i<Vector2.size();i++) {
            vector2[i] = Vector2.get(i);
        }

        //Calculating cosine similarity
        for (int i=0;i<vector1.length;i++) {
            sum1= sum1+ Math.pow(vector1[i], 2);
            sum2= sum2+Math.pow(vector2[i], 2);
            mul= mul+(vector1[i] * vector2[i]);
        }
        //Square roots
        sum1 = Math.sqrt(sum1);
        sum2 = Math.sqrt(sum2);

        if (sum1 != 0.0 | sum2 != 0.0) {
            cos_sim_value = mul / (sum1 * sum2);
        } else {
            return 0.0;
        }
        return cos_sim_value;
    }
}










