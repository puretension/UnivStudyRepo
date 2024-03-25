// 2020111133 컴퓨터공학과 이도형
#include<bits/stdc++.h>
using namespace std;
#define NUM_OF_KEYS 10

void Merge(int arr[], int left, int mid, int right) {
    int sorted[NUM_OF_KEYS];
    int i, l, r, k;
    // 왼쪽 부분배열의 시작인덱스 l, 오른쪾 부분배열 시작인덱스 r, 합병된 배열의 현재인덱스 k
    l = left; r = mid + 1; k = left;
    // 두 부분 배열의 요소들을 비교하며 합병(오름차순으로)
    while (l <= mid && r <= right) {
        if (arr[l] <= arr[r]) sorted[k++] = arr[l++];
        else sorted[k++] = arr[r++];
    }
     // 남은 왼쪽 부분 배열의 요소들을 추가
    while (l <= mid) sorted[k++] = arr[l++];
     // 남은 오른쪽 부분 배열의 요소들을 추가
    while (r <= right) sorted[k++] = arr[r++];
    
    // 임시 배열의 요소들을 원래 배열에 복사
    for (i = left; i <= right; i++) arr[i] = sorted[i];
    
    // 출력: 합병된 배열 상태
   cout << "After merging: ";
    for(i = left; i <= right; i++) {
        cout << arr[i] << " ";
    }
    cout << "\n";
}

void MergeSort(int arr[], int Left, int Right) {
    if (Left < Right) {
        int Mid = (Left + Right) / 2;
        // 왼쪽 부분을 재귀적으로 정렬
        MergeSort(arr, Left, Mid); 
         // 오른쪽 부분을 재귀적으로 정렬
        MergeSort(arr, Mid + 1, Right);
        Merge(arr, Left, Mid, Right);
    }
}

void printArray(int arr[], int size) {
    for (int i = 0; i < size; i++) {
            cout << arr[i] << " ";
    }
    cout << "\n";
}

// 비순환적 합병 정렬은 재귀 호출을 사용하지 않고 반복문을 통해 배열을 정렬하는 방식
void NonRecursiveMergeSort(int arr[], int n) {
    int curSize; 
    int leftStart;
 
    // 부분 배열의 크기를 1부터 시작해서 n-1까지 증가시키며 합병을 수행
    for (curSize = 1; curSize <= n-1; curSize = 2*curSize) {
        // 배열의 크기가 1로 시작(cursize =  1)하여,  부분 배열들을 합병하여 크기를 점점 늘려가며 전체 배열을 정렬
        for (leftStart = 0; leftStart < n-1; leftStart += 2*curSize) {
             // 중간 지점과 오른쪽 끝을 계산
            int mid = min(leftStart + curSize - 1, n-1);
            int rightEnd = min(leftStart + 2*curSize - 1, n-1);
             // 합병 함수를 호출하여 부분 배열을 합병
            Merge(arr, leftStart, mid, rightEnd);
            cout << "현재 배열 상태: ";
            printArray(arr, n);
        }
    }
}

int main() {
    cout << "Code By 2020111133 컴퓨터공학과 이도형\n";
    
    int arr[NUM_OF_KEYS] = {30, 20, 40, 35, 5, 50, 45, 10, 25, 15};
    cout << "최초 배열: ";
    printArray(arr, NUM_OF_KEYS);

    // 순환적 합병 정렬 함수를 호출
    MergeSort(arr, 0, NUM_OF_KEYS-1);
    cout << "합병정렬 이후 배열: ";
    printArray(arr, NUM_OF_KEYS);
    
    // // 비순환적 합병 정렬 함수를 호출
    // NonRecursiveMergeSort(arr, NUM_OF_KEYS);
    // cout << "비순환적 합병정렬 이후 배열: ";
    // printArray(arr, NUM_OF_KEYS);

    return 0;
}