public class getBoy {
    public static void main(String[] args) {

        System.out.println(getBoy.solution(4,"FMFM"));
    }
    public static int solution(int n, String s) {
        // 请添加具体实现
        int bTotal = 0,gTotal = 0; //记录男孩所在位置索引的和
        int boyCount = 0; //记录男孩的个数
        int girlCount = 0;
        char[] queue=s.toCharArray();

        for(int i = 0; i < queue.length; i++){
            if(queue[i] == 'M'){
                boyCount++;
                bTotal += i;
            }
            if(queue[i] == 'F'){
                girlCount++;
                gTotal += i;
            }
        }
        //如果把男孩放在左边
        bTotal = bTotal - (boyCount - 1) * boyCount / 2;
        gTotal = gTotal - (girlCount - 1) * girlCount / 2;//如果把女孩放在左边
        return bTotal > gTotal ? gTotal: bTotal;
    }
}