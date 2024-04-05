package com.pru.lib;

public class MinimumAbsSumOfThreeArrays {
    public static void main(String[] args) {
        int[] array1 = {3, 8, 1, 9};
        int[] array2 = {2, 5, 4, 7};
        int[] array3 = {6, 1, 9, 12};

        Triple<Integer, Integer, Integer> result = findMinimumAbsSum(array1, array2, array3);

        System.out.println("Numbers with minimum abs sum: " +
                result.getFirst() + ", " + result.getSecond() + ", " + result.getThird());
    }

    public static Triple<Integer, Integer, Integer> findMinimumAbsSum(int[] arr1, int[] arr2, int[] arr3) {
        int minSum = Integer.MAX_VALUE;
        int resultA = 0, resultB = 0, resultC = 0;

        for (int a : arr1) {
            for (int b : arr2) {
                for (int c : arr3) {
                    int currentSum = Math.abs(a - b) + Math.abs(b - c) + Math.abs(c - a);

                    if (currentSum < minSum) {
                        minSum = currentSum;
                        resultA = a;
                        resultB = b;
                        resultC = c;
                    }
                }
            }
        }

        return new Triple<>(resultA, resultB, resultC);
    }

    static class Triple<A, B, C> {
        private final A first;
        private final B second;
        private final C third;

        public Triple(A first, B second, C third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        public A getFirst() {
            return first;
        }

        public B getSecond() {
            return second;
        }

        public C getThird() {
            return third;
        }
    }
}
