package com.example;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import  org.json.*;

public class TopArticles {
    public static String fetchData(String username, int page) throws IOException {
        String url = "https://jsonmock.hackerrank.com/api/articles?author=" + username + "&page=" + page;
        URL url1 = new URL(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url1.openConnection();
        httpURLConnection.setRequestMethod("GET");

        if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else {
            throw new RuntimeException("Failed : HTTP error code : " + httpURLConnection.getResponseCode());
        }
    }


    public static List<String> TopArticles (String username, int limit) throws IOException {
        List<Article> articleList = new ArrayList<>();
        int page = 1;
        boolean hasMorePages = true;

        while (hasMorePages){ // iterasi ini digunakan untuk ngeloop semua pages
            String response = fetchData(username, page);
            if (response == null) break;//buat sebuah langkah untuk menghentikan looping yaitu ketika tidak ada response yang diterima lagi

            //get json response
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("data"); //dapatkan "data" dari json response tadi

            //sekarang iterasi semua data dari json array yang didapat dari fetch data tadi
            for (int i = 0 ; i < jsonArray.length() ; i++ ){ //sementara iterasi ini digunakan untuk ngeloop semua data dalam data array yang berisi response data
                JSONObject jsonArticle = jsonArray.getJSONObject(i); //ambil hasil dari jsonarray
                String title = jsonArticle.optString("title", null); //inisialisasi title yang diambil dari respon dengan nama title
                String storyTitle = jsonArticle.optString("story_title", null); //inisialisasi story_title yang diambil dari article dengan kata kuncinya tadi story_title
                String articleName = title != null ? title : storyTitle ; //by deafult, response title akan diambil dari title, klo title kosong maka diambil dari story_title
                if (articleName == null) continue; //ini untuk mendapatkan article name

                //sekarang bagaimana dengan number of comment
                int numComments = jsonArticle.optInt("num_comments", 0); //inisialisasi numcomment diambil dari num_comment

                //sekarang build dia pakai constructor dan masukkan ke array
                articleList.add(new Article(articleName, numComments));
            }

            //sekarang, selama total_pages masih belum kurang dari per_pages (contohnya harusnya dalam satu page ada 10 konten, trnyata kontennya kurang dari 10,
            //ini artinya bahwa, dibelakangnya gada lagi konten. jadi has more pages akan jadi false apabila total_pages<per_page, logikanya gitu dulu
            //nah disini ini salahnya, total_pages itu bukan total konten dalam satu page itu, tapi memang total page nya
            //total konten itu ada di kata kunci total
            //total konten dalam page itu gada diatur specifically
            //ya jadi benar, hasmorepages akan tetap true apabila "page" kita saat ini lebih kecil dari "total_pages"
            //apabila nomor "page" kita sama dengan "total_pages" maka page itu adalah page terakhir, dan penambahan page (iterasi page) akan terus dilakukan selama "page" masih < "total_page"

            hasMorePages = page < jsonObject.optInt("total_pages");
            page++;
        }

        articleList.sort(( a,  b) -> {
            if (a.getNumComments() != b.getNumComments()){
                return Integer.compare(b.getNumComments(), a.getNumComments());
            } else {
                return a.getName().compareTo(b.getName());
            }
        }); //buat comparator , apabila number tidak sama maka yang dicompare num coment
        //sebaliknya, maka yang di compare namanya

        //sekarang buat list result yang hanya menampilkan nama saja
        List<String> topArticleList = new ArrayList<>();
        //sekarang iterasi sampai ke limit yang disediakan
        for (int i = 0; i < Math.min(limit, articleList.size()); i++){
            topArticleList.add(articleList.get(i).getName());
        }
        return topArticleList;
    }




}
