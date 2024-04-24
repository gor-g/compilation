; expression prefixee parenthesee : (; (let a input) (; (let b input) (; (while (< 0 b) (; (let aux (% a b)) (; (let a b) (let b aux)))) (output a))))
DATA SEGMENT
	a DD
	b DD
	aux DD
DATA ENDS
CODE SEGMENT
	in eax
	mov a, eax
	in eax
	mov b, eax
debut_while_1:
	mov eax, 0
	push eax
	mov eax, b
	pop ebx
	sub eax, ebx
	jle faux_lt_1
	mov eax, 1
	jmp sortie_lt_1
faux_lt_1 :
	mov eax, 0
sortie_lt_1 :
	jz sortie_while_1
	mov eax, b
	push eax
	mov eax, a
	pop ebx
	mov ecx, eax
	div ecx, ebx
	mul ecx, ebx
	sub eax, ecx
	mov aux, eax
	mov eax, b
	mov a, eax
	mov eax, aux
	mov b, eax
	jmp debut_while_1
sortie_while_1:
	mov eax, a
	out eax
CODE ENDS