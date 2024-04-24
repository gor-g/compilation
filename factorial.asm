; expression prefixee parenthesee : (; (let n input) (; (let result 1) (; (while (> n 0) (; (let result (* result n)) (let n (- n 1)))) (output result))))
DATA SEGMENT
	n DD
	result DD
DATA ENDS
CODE SEGMENT
	in eax
	mov n, eax
	mov eax, 1
	mov result, eax
debut_while_1:
	mov eax, n
	push eax
	mov eax, 0
	pop ebx
	sub eax, ebx
	jge faux_gt_1
	mov eax, 1
	jmp sortie_gt_1
faux_gt_1 :
	mov eax, 0
sortie_gt_1 :
	jz sortie_while_1
	mov eax, result
	push eax
	mov eax, n
	pop ebx
	mul eax, ebx
	mov result, eax
	mov eax, n
	push eax
	mov eax, 1
	pop ebx
	sub ebx, eax
	mov eax, ebx
	mov n, eax
	jmp debut_while_1
sortie_while_1:
	mov eax, result
	out eax
CODE ENDS