.section .rodata
fmt:
   .asciz "%d\n"
eol:
   .asciz "\n"

.section .text
.globl print

print:
 push %rbp
 mov %rsp, %rbp

 mov $fmt, %rdi
 movq $1, %rax
 xor %rdx, %rdx

check_stack_alignment:
    movq %rsp, %rax      # Move stack pointer to %rax
    andq $15, %rax       # Perform bitwise "and" with 15 (binary 1111)
    testq %rax, %rax
    jz aligned
    xor %rax, %rax
    push %rax

aligned:
 call printf

  cmp $0, %rax
  jne return
  pop %rax
return:
 mov %rbp, %rsp
 pop %rbp
 ret 

