# Video-Game-Recommender-System

The Video Game Recommender System shows top 10 games list based on user reviews. The Game Finder App use Metacritic Video Game Comments. To find top 10 games, the app use inverted term frequency for finiding similarities between user query and and game.  

First, we need to load user comments and game titles.To load data, we will first load each line, then we will spilt the data to get title and user comments. 
        
        
        inputStream2 = getResources().openRawResource(R.raw.data2);// Loading each line
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(inputStream2)); //Loading each line
        while (index < data2_line.size()) { //Spliting the data to get title and user reviews
            temp3=data2_line.get(index).split(",");
            game_number2.add(temp3[0]);
            title_data2.add(temp3[1]);
            platform2.add(temp3[2]);
            userscore2.add(temp3[3]);
            review2.add(temp3[4]);
            index++;
        }
        
        
After loading the loading the data, we will remove stopwords and tokenize each word. 

        while (index < title_data2.size()) {
            Pattern p =                                Pattern.compile("\\b(i|me|my|myself|we|our|ours|ourselves|you|your|yours|yourselves|he|him|she|her|himself|herself|his|" +
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
                    tokenized.add(temp_token);}
            }
            tokenized_review.add(tokenized);
            index++;
        }
        
        
        
Now, we have created our token. We will use tokens to find TF_IDF value. We created two functions for tf and idf which will return tf and idf scores.
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
    
![Alt text](Image/formula1.JPG?raw=true "Title")

