/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PlanetVM;

import java.util.*;
import PlanetVM.Functions.*;

/**
 *
 * @author GAMER
 */
public class Runtime {
    public long[] Memory;
    public Deque<Long> stack = new ArrayDeque<Long>();
    public Map<String, Long> registers = new HashMap<>();

    public Runtime(int Memory_size)
    {
        Memory = new long[Memory_size];
    }

    public void ExecuteInstruction(String instruction)
    {
        // split on the spaces and trim
        String[] Line = instruction.trim().split("\\s+");
        if (Line.length == 0) return;

        switch (Line[0].toLowerCase())
        {
            case "push":
                if (Line.length > 1) {
                    try {
                        long value = Long.parseLong(Line[1]);
                        stack.push(value);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number for push: " + Line[1]);
                    }
                }
                break;
            case "pop":
                if (!stack.isEmpty()) {
                    stack.pop();
                }
                break;
            case "dup":
                if (!stack.isEmpty()) {
                    stack.push(stack.peek());
                }
                break;
            case "swap":
                if (stack.size() >= 2) {
                    long a = stack.pop();
                    long b = stack.pop();
                    stack.push(a);
                    stack.push(b);
                }
                break;
            case "add":
                if (stack.size() >= 2) {
                    long a = stack.pop();
                    long b = stack.pop();
                    stack.push(a + b);
                }
                break;
            case "sub":
                if (stack.size() >= 2) {
                    long a = stack.pop();
                    long b = stack.pop();
                    stack.push(b - a);
                }
                break;
            case "mul":
                if (stack.size() >= 2) {
                    long a = stack.pop();
                    long b = stack.pop();
                    stack.push(a * b);
                }
                break;
            case "div":
                if (stack.size() >= 2) {
                    long a = stack.pop();
                    long b = stack.pop();
                    if (a != 0) {
                        stack.push(b / a);
                    } else {
                        System.err.println("Division by zero");
                        stack.push(b); // restore stack
                        stack.push(a);
                    }
                }
                break;
            case "load":
                if (Line.length > 1) {
                    try {
                        int addr = Integer.parseInt(Line[1]);
                        if (addr >= 0 && addr < Memory.length) {
                            stack.push(Memory[addr]);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid address for load: " + Line[1]);
                    }
                }
                break;
            case "store":
                if (Line.length > 1 && !stack.isEmpty()) {
                    try {
                        int addr = Integer.parseInt(Line[1]);
                        if (addr >= 0 && addr < Memory.length) {
                            Memory[addr] = stack.pop();
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid address for store: " + Line[1]);
                    }
                }
                break;
            case "print":
                if (!stack.isEmpty()) {
                    System.out.println(stack.peek());
                }
                break;
            case "clear":
                stack.clear();
                break;
            default:
                System.err.println("Unknown instruction: " + Line[0]);
                break;
        }
    }

    public String getStackAsString() {
        return stack.toString();
    }

    public String getMemoryAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < java.lang.Math.min(Memory.length, 10); i++) {
            if (i > 0) sb.append(", ");
            sb.append(Memory[i]);
        }
        if (Memory.length > 10) sb.append(", ...");
        sb.append("]");
        return sb.toString();
    }
}
