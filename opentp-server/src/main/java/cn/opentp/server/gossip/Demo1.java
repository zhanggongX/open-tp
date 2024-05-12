package cn.opentp.server.gossip;


import cn.opentp.gossip.GossipManagement;
import cn.opentp.gossip.GossipProperties;
import cn.opentp.gossip.GossipService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Demo1 {

    public static void main(String[] args) {

        Demo1 demo1 = new Demo1();
        List<List<Integer>> list = new ArrayList<>();
//        [[9,5,7,3],[8,9,6,1],[6,7,14,3],[2,5,3,1]]
        //[[4,3,2],[3,2,1]]
//        List<Integer> list1 = Arrays.asList(9,5,7,3);
//        List<Integer> list2 = Arrays.asList(8,9,6,1);
//        List<Integer> list3 = Arrays.asList(6,7,14,3);
//        List<Integer> list4 = Arrays.asList(2,5,3,1);

        List<Integer> list1 = Arrays.asList(4, 3, 2);
        List<Integer> list2 = Arrays.asList(3, 2, 1);

        list.add(list1);
        list.add(list2);
//        list.add(list3);
//        list.add(list4);

        int i = demo1.maxScore(list);
        System.out.println(i);
    }

    public int maxScore(List<List<Integer>> grid) {
        int row = grid.size();
        int col = grid.get(0).size();
        int[][] ar = new int[row][col];

        List<Integer> list0 = grid.get(0);
        int res = Integer.MIN_VALUE;

        ar[0][0] = list0.get(0);
        for(int i = 1; i < list0.size(); i++){
            int now = list0.get(i);
            res = Math.max(res, now - ar[0][i-1]);
            ar[0][i] = Math.min(ar[0][i-1], now);
        }

        for(int i = 1; i < grid.size(); i++){
            int now = grid.get(i).get(0);
            res = Math.max(res, now - ar[i-1][0]);
            ar[i][0] = Math.min(ar[i-1][0], now);
        }

        for(int i = 1; i < grid.size(); i++){
            List<Integer> listi = grid.get(i);
            for(int j = 1; j < listi.size(); j++){
                int now = listi.get(j);
                int min = Math.min(ar[i][j-1], ar[i-1][j]);
                res = Math.max(res, now - min);
                ar[i][j] = Math.min(min, now);
            }
        }

        return res;
    }


}
