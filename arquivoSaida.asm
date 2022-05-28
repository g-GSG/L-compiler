section .data 
M: 
resb 10000h
resd 1
resd 1
resd 1
section .text 
global _start
_start:
mov eax,0
mov [qword M +0],eax
mov eax, 'k'
mov [qword M +4],eax
mov eax,5
mov [qword M +8],eax
mov eax, 'k'
mov [qword M +12],eax
mov eax,1
mov [qword M +0],eax
mov eax, [qword M+0h]
mov ebx, [qword M+0]
add eax,ebx
mov [qword M+0], eax