package demo;

import java.util.Arrays;

public class Hello {
    public static void main(String[] args) {
        //        Write in the .txt files
        Filereader fr = new Filereader();

//        Import the data path
        String pathname = "src/demo/Dataset1.txt";
        String pathname2 = "src/demo/Dataset2.txt";
        String pathname3 = "src/demo/Dataset3.txt";
        double[][] temp = fr.getdata(pathname,3);
        double [][] temp2 = fr.getdata(pathname2,5);
        double [][] temp3 = fr.getdata(pathname3,9);

        System.out.println("For Dataset-1:");
        getInfo(temp,16*16);

        System.out.println("For Dataset-2: ");
        getInfo(temp2,24*24);
//
        System.out.println("For Dataset-3: ");
        getInfo(temp3,12*12);
    }

    /**
     *
     * @param weight
     * @param temp
     * @return 返回某一weight下，对于某数据集temp的accuracy
     */
    public static double getAcc(double[] weight, double[][] temp) {
        int acc = 0;
        for (int i = 0; i < temp.length; i++) {
            double sum = 0;
            for (int j = 0; j < temp[1].length-1; j++) {
                sum = sum + temp[i][j] * weight[j];
            }
            if (sum * temp[i][temp[i].length - 1] < 0) {
                acc++;
            }
        }
//        计算当前w的accuracy；
        double accRate = 1 - acc / temp.length;
        return accRate;
    }

    /**
     *
     * @param temp
     * @param gammaGuess
     * @param pre_w
     * @return 对于某一weight，以及gammaGuess，在数据集temp下对于weight的更新
     */
    public static double[] getWeight(double R_square,double [][] temp, double gammaGuess, double[] pre_w) {
//        计算最大iteration次数
        double maxIteration = 12 * R_square / (gammaGuess*gammaGuess);
        int r_len = temp.length;
        int col_len = temp[1].length;
//        初始化iteration次数以及代表遍历结束的flag：finish
        int finish = 0;
        int iteration = 0;
        System.out.println("The maximum iteration time is: ："+maxIteration);
        while (finish == 0 && iteration < maxIteration) {
//            从数据集第一行开始进行遍历
            for (int i = 0; i < r_len; i++) {
//                判断某一点为violation pts，并进行对于weight的更新
                    double sum1 = 0;
                    double sum2 = 0;
                    for (int j =0; j<col_len-1;j++){
                        sum1 += pre_w[j]*pre_w[j];
                        sum2 += pre_w[j]*temp[i][j];
                    }
                    double distDown = Math.sqrt(sum1);
                    double distUp = Math.abs(sum2);
                    double margin = distUp/distDown;
//                    第一种情况：方向问题
//                    若w*p与label的乘积若为正，则说明不是violation points,
//                    当w*p与label的乘积为0或负数，则证明该点为violation points
                    if(sum2*temp[i][col_len-1] <= 0){
                        iteration ++;
                        for(int j =0; j<col_len-1;j++){
                            pre_w[j] = pre_w[j] + temp[i][col_len-1]*temp[i][j];
                        }
//                        System.out.println("方向问题,更新后weight："+Arrays.toString(pre_w));
//                        System.out.println("当前iteration：" +iteration);
//                        System.out.println("当前为第" + (i+1) +"行");
//                        更新当前weight后，跳出从数据集第一行进行的遍历，重新开始对于数据集的检查
                        break;}
                    else if (margin < gammaGuess/2) {
                        iteration++;
                        for (int j = 0; j < col_len - 1; j++) {
                            pre_w[j] = pre_w[j] + temp[i][col_len - 1] * temp[i][j];
                        }
//                        System.out.println("margin问题: 更新后weight为: " + Arrays.toString(pre_w));
//                        System.out.println("当前为第" +(i+1)+"行");
//                        System.out.println("当前iteration：" +iteration);
                        break;
                    }
//                当已经遍历到最后一条数据且iteration time小于max，则跳出当前对于数据集的遍历，返回当前weight
                    else if (i == r_len-1) {
                        finish = 1;
                        break;
                    }
            }
        }
//        System.out.println("finish为："+finish);
//        System.out.println("结束时的iteration为："+iteration);
        if(finish == 0){
            System.out.println("Wrong Guess!");
        }
        return pre_w;
    }

    /**
     *
     * @param R_square
     * @param temp
     * @param gammaGuess
     * @param pre_w
     * @return if the current gamma for dataset ca terminate successfully
     */
    public static boolean isAcceptable(double R_square,double[][] temp, double gammaGuess,double[] pre_w){
        //        计算最大iteration次数
        boolean isAcc = false;
        double maxIteration = 12 * R_square / (gammaGuess*gammaGuess);
        int r_len = temp.length;
        int col_len = temp[1].length;
        int finish = 0;
        int iteration = 0;
        while (finish == 0 && iteration < maxIteration) {
//            从数据集第一行开始进行遍历
            for (int i = 0; i < r_len; i++) {
//                判断某一点为violation pts，并进行对于weight的更新
                double sum1 = 0;
                double sum2 = 0;
                for (int j =0; j<col_len-1;j++){
                    sum1 += pre_w[j]*pre_w[j];
                    sum2 += pre_w[j]*temp[i][j];
                }
                double distDown = Math.sqrt(sum1);
                double distUp = Math.abs(sum2);
                double margin = distUp/distDown;
//                    第一种情况：方向问题
//                    若w*p与label的乘积若为正，则说明不是violation points,
//                    当w*p与label的乘积为0或负数，则证明该点为violation points
                if(sum2*temp[i][col_len-1] <= 0){
                    iteration ++;
                    for(int j =0; j<col_len-1;j++){
                        pre_w[j] = pre_w[j] + temp[i][col_len-1]*temp[i][j];
                    }
                    break;}
                else if (margin < gammaGuess/2) {
                    iteration++;
                    for (int j = 0; j < col_len - 1; j++) {
                        pre_w[j] = pre_w[j] + temp[i][col_len - 1] * temp[i][j];
                    }
                    break;
                }
                else if (i == r_len-1) {
                    finish = 1;
                    break;
                }
            }
        }
        if(finish == 0){
            isAcc = false;
        }
        else if(finish == 1){
            isAcc = true;
        }
        return isAcc;
    }
    /**
     *
     * @param temp
     * @param weight
     * @return 返回在某一weight下，关于数据集temp的最小margin
     */
    public static double getMargin(double [][] temp, double[] weight){
        double margin = 10000;
        for(int i =0;i< temp.length;i++){
            double sum1 = 0;
            double sum2 = 0;
            for (int j =0; j<weight.length;j++){
                sum1 += weight[j]*weight[j];
                sum2 += weight[j]*temp[i][j];
            }
            double distDown = Math.sqrt(sum1);
            double distUp = Math.abs(sum2);
            if(distUp/distDown < margin){
                margin = distUp/distDown;
            }
        }
        return margin;
    }

    /**
     *
     * @param temp
     * @param
     */
    public static void getInfo(double [][] temp, double R_square){
        double[] w = new double[temp[1].length-1];
        for (int i = 0; i < temp[1].length-1; i++) {
            w[i] = 0;
        }
        double gammaGuess = Math.sqrt(R_square);
        boolean isAcceptable = false;
        while (isAcceptable == false){
            gammaGuess = gammaGuess/2;
            isAcceptable = isAcceptable(R_square,temp,gammaGuess,w);
        }
        double[] w1 = new double[temp[1].length-1];
        for (int i = 0; i < temp[1].length-1; i++) {
            w1[i] = 0;
        }
        System.out.println("The final gamma guess is: "+gammaGuess);
        double [] testWeight = getWeight(R_square,temp,gammaGuess,w1);
        System.out.println("The weight is: " + Arrays.toString(testWeight));
        double accuracy = getAcc(testWeight,temp);
        System.out.println("The data accuracy is: " + accuracy);
        double margin = getMargin(temp,testWeight);
        System.out.println("The minimal margin is: " + margin+"\n");
    }

}


