// 2020111133 컴퓨터공학과 이도형
#include<bits/stdc++.h>
using namespace std;
// SWAP 매크로함수 정의
#define SWAP(a, b) do { int SWAP_temp = a; a = b; b = SWAP_temp; } while (0)

void printArray(int A[], int size) {
    for (int i = 0; i < size; i++) {
        cout << A[i] << " ";
    }
    cout << "\n";
}

// 주어진 루트를 가지는 부분 트리가 힙 속성을 유지하도록 만드는 MakeHeap
void MakeHeap(int A[], int root, int lastNode) {
    int parent, leftSon, rightSon, son, rootValue;
    parent = root; // 현재 부모 노드
    rootValue = A[root]; // 현재 부모 노드의 값을 저장
    leftSon = 2 * parent + 1; // 왼쪽 자식 노드의 인덱스
    rightSon = leftSon + 1; // 오른쪽 자식 노드의 인덱스
     // 자식 노드가 마지막 노드 이내인 동안 반복
    while (leftSon <= lastNode) {
        // 오른쪽 자식이 존재하고 왼쪽 자식보다 크면 오른쪽 자식을 선택
        if (rightSon <= lastNode && A[leftSon] < A[rightSon])
            son = rightSon;
        else
            son = leftSon;
        // 자식 노드의 값이 부모 노드의 값보다 크면 교환
        if (rootValue < A[son]) {
            A[parent] = A[son];
            parent = son;
             // 새로운 자식 노드들의 인덱스를 계산
            leftSon = 2 * parent + 1;
            rightSon = leftSon + 1;
        } else
            break;
    }
    // 최종적으로 부모 노드의 값을 저장
    A[parent] = rootValue;
}

void HeapSort(int A[], int n) {
     int i;
    // 힙 생성 과정
    for (int i = n / 2 - 1; i >= 0; i--) {
        MakeHeap(A, i, n - 1);
        cout << "각 Turn마다 힙생성 후의 배열 " <<": ";
        printArray(A, n);
    }

    // HeapSort
    for(i = n/2; i>=0; i--) MakeHeap(A, i, n-1);
    for(i = n-1; i>0; i--){
        // 배열의 첫 번째 요소(가장 큰 값)를 배열의 마지막 요소와 교환
        SWAP(A[0], A[i]);
         MakeHeap(A,0,i-1);
                  cout << "각 Turn마다 힙정렬 후의 배열 " <<": ";
         printArray(A, n);
    }
}

int main() {
    cout << "Code By 2020111133 컴퓨터공학과 이도형\n";
    
    int A[] = {4, 1, 3, 2, 16, 9, 10, 14, 8, 7};
    int n = sizeof(A)/sizeof(A[0]);
    
    cout << "최초 배열: ";
    printArray(A, n);
    
    HeapSort(A, n);
    
    cout << "힙정렬 배열: ";
    printArray(A, n);
    
    return 0;
}