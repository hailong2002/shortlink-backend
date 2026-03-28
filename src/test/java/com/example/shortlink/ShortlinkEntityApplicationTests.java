//package com.example.shortlink;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//class ShortlinkEntityApplicationTests {
////
////	@Test
////	void contextLoads() {
////	}
//
//    @Test
//    void tactotoe() {
//        char[][] arr =  {
//                {'x','o','x'},
//                {'x','x','o'},
//                {'x','o','o'},
//        };
//        isWin(arr, 'x');
//    }
//
//    void isWin(char[][] arr, char p) {
//        isAnNgang(arr, p);
//        isAnDoc(arr, p);
//        isAnCheo(arr, p);
//    }
//
//    void isAnNgang(char[][] arr, char p) {
//        int i = 0, j = 0;
//        if (arr[i][j] == p && arr[i+1][j] == p && arr[i+2][j] == p) {
//            System.out.print(p + " win");
//        } else if (arr[i][j+1] == p && arr[i+1][j+1] == p && arr[i+2][j+1] == p) {
//            System.out.print(p + " win");
//        } else if (arr[i][j+2] == p && arr[i+1][j+2] == p && arr[i+2][j+2] == p) {
//            System.out.print(p + " win");
//        }
//    }
//
//    void isAnDoc(char[][] arr, char p) {
//        int i = 0, j = 0;
//        if (arr[i][j] == p && arr[i][j+1] == p && arr[i][j+2] == p) {
//            System.out.print(p + " win");
//        } else if (arr[i+1][j] == p && arr[i+1][j+1] == p && arr[i+1][j+2] == p) {
//            System.out.print(p + " win");
//        } else if (arr[i+2][j] == p && arr[i+2][j+1] == p && arr[i+2][j+2] == p) {
//            System.out.print(p + " win");
//        }
//    }
//
//    void isAnCheo(char[][] arr, char p) {
//        int i = 0, j = 0;
//        if (arr[i][j] == p && arr[i+1][j+1] == p && arr[i+2][j+2] == p) {
//            System.out.print(p + " win");
//        } else if (arr[i+2][j] == p && arr[i+1][j+1] == p && arr[i][j+2] == p) {
//            System.out.print(p + " win");
//        }
//    }
//
//
//}
