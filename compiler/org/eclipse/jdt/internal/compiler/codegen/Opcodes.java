package org.eclipse.jdt.internal.compiler.codegen;
/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
import org.eclipse.jdt.internal.compiler.*;

public interface Opcodes {

	public static final byte OPC_nop = 0;
	public static final byte OPC_aconst_null = 1;
	public static final byte OPC_iconst_m1 = 2;
	public static final byte OPC_iconst_0 = 3;
	public static final byte OPC_iconst_1 = 4;
	public static final byte OPC_iconst_2 = 5;
	public static final byte OPC_iconst_3 = 6;
	public static final byte OPC_iconst_4 = 7;
	public static final byte OPC_iconst_5 = 8;
	public static final byte OPC_lconst_0 = 9;
	public static final byte OPC_lconst_1 = 10;
	public static final byte OPC_fconst_0 = 11;
	public static final byte OPC_fconst_1 = 12;
	public static final byte OPC_fconst_2 = 13;
	public static final byte OPC_dconst_0 = 14;
	public static final byte OPC_dconst_1 = 15;
	public static final byte OPC_bipush = 16;
	public static final byte OPC_sipush = 17;
	public static final byte OPC_ldc = 18;
	public static final byte OPC_ldc_w = 19;
	public static final byte OPC_ldc2_w = 20;
	public static final byte OPC_iload = 21;
	public static final byte OPC_lload = 22;
	public static final byte OPC_fload = 23;
	public static final byte OPC_dload = 24;
	public static final byte OPC_aload = 25;
	public static final byte OPC_iload_0 = 26;
	public static final byte OPC_iload_1 = 27;
	public static final byte OPC_iload_2 = 28;
	public static final byte OPC_iload_3 = 29;
	public static final byte OPC_lload_0 = 30;
	public static final byte OPC_lload_1 = 31;
	public static final byte OPC_lload_2 = 32;
	public static final byte OPC_lload_3 = 33;
	public static final byte OPC_fload_0 = 34;
	public static final byte OPC_fload_1 = 35;
	public static final byte OPC_fload_2 = 36;
	public static final byte OPC_fload_3 = 37;
	public static final byte OPC_dload_0 = 38;
	public static final byte OPC_dload_1 = 39;
	public static final byte OPC_dload_2 = 40;
	public static final byte OPC_dload_3 = 41;
	public static final byte OPC_aload_0 = 42;
	public static final byte OPC_aload_1 = 43;
	public static final byte OPC_aload_2 = 44;
	public static final byte OPC_aload_3 = 45;
	public static final byte OPC_iaload = 46;
	public static final byte OPC_laload = 47;
	public static final byte OPC_faload = 48;
	public static final byte OPC_daload = 49;
	public static final byte OPC_aaload = 50;
	public static final byte OPC_baload = 51;
	public static final byte OPC_caload = 52;
	public static final byte OPC_saload = 53;
	public static final byte OPC_istore = 54;
	public static final byte OPC_lstore = 55;
	public static final byte OPC_fstore = 56;
	public static final byte OPC_dstore = 57;
	public static final byte OPC_astore = 58;
	public static final byte OPC_istore_0 = 59;
	public static final byte OPC_istore_1 = 60;
	public static final byte OPC_istore_2 = 61;
	public static final byte OPC_istore_3 = 62;
	public static final byte OPC_lstore_0 = 63;
	public static final byte OPC_lstore_1 = 64;
	public static final byte OPC_lstore_2 = 65;
	public static final byte OPC_lstore_3 = 66;
	public static final byte OPC_fstore_0 = 67;
	public static final byte OPC_fstore_1 = 68;
	public static final byte OPC_fstore_2 = 69;
	public static final byte OPC_fstore_3 = 70;
	public static final byte OPC_dstore_0 = 71;
	public static final byte OPC_dstore_1 = 72;
	public static final byte OPC_dstore_2 = 73;
	public static final byte OPC_dstore_3 = 74;
	public static final byte OPC_astore_0 = 75;
	public static final byte OPC_astore_1 = 76;
	public static final byte OPC_astore_2 = 77;
	public static final byte OPC_astore_3 = 78;
	public static final byte OPC_iastore = 79;
	public static final byte OPC_lastore = 80;
	public static final byte OPC_fastore = 81;
	public static final byte OPC_dastore = 82;
	public static final byte OPC_aastore = 83;
	public static final byte OPC_bastore = 84;
	public static final byte OPC_castore = 85;
	public static final byte OPC_sastore = 86;
	public static final byte OPC_pop = 87;
	public static final byte OPC_pop2 = 88;
	public static final byte OPC_dup = 89;
	public static final byte OPC_dup_x1 = 90;
	public static final byte OPC_dup_x2 = 91;
	public static final byte OPC_dup2 = 92;
	public static final byte OPC_dup2_x1 = 93;
	public static final byte OPC_dup2_x2 = 94;
	public static final byte OPC_swap = 95;
	public static final byte OPC_iadd = 96;
	public static final byte OPC_ladd = 97;
	public static final byte OPC_fadd = 98;
	public static final byte OPC_dadd = 99;
	public static final byte OPC_isub = 100;
	public static final byte OPC_lsub = 101;
	public static final byte OPC_fsub = 102;
	public static final byte OPC_dsub = 103;
	public static final byte OPC_imul = 104;
	public static final byte OPC_lmul = 105;
	public static final byte OPC_fmul = 106;
	public static final byte OPC_dmul = 107;
	public static final byte OPC_idiv = 108;
	public static final byte OPC_ldiv = 109;
	public static final byte OPC_fdiv = 110;
	public static final byte OPC_ddiv = 111;
	public static final byte OPC_irem = 112;
	public static final byte OPC_lrem = 113;
	public static final byte OPC_frem = 114;
	public static final byte OPC_drem = 115;
	public static final byte OPC_ineg = 116;
	public static final byte OPC_lneg = 117;
	public static final byte OPC_fneg = 118;
	public static final byte OPC_dneg = 119;
	public static final byte OPC_ishl = 120;
	public static final byte OPC_lshl = 121;
	public static final byte OPC_ishr = 122;
	public static final byte OPC_lshr = 123;
	public static final byte OPC_iushr = 124;
	public static final byte OPC_lushr = 125;
	public static final byte OPC_iand = 126;
	public static final byte OPC_land = 127;
	public static final byte OPC_ior = (byte) 128;
	public static final byte OPC_lor = (byte) 129;
	public static final byte OPC_ixor = (byte) 130;
	public static final byte OPC_lxor = (byte) 131;
	public static final byte OPC_iinc = (byte) 132;
	public static final byte OPC_i2l = (byte) 133;
	public static final byte OPC_i2f = (byte) 134;
	public static final byte OPC_i2d = (byte) 135;
	public static final byte OPC_l2i = (byte) 136;
	public static final byte OPC_l2f = (byte) 137;
	public static final byte OPC_l2d = (byte) 138;
	public static final byte OPC_f2i = (byte) 139;
	public static final byte OPC_f2l = (byte) 140;
	public static final byte OPC_f2d = (byte) 141;
	public static final byte OPC_d2i = (byte) 142;
	public static final byte OPC_d2l = (byte) 143;
	public static final byte OPC_d2f = (byte) 144;
	public static final byte OPC_i2b = (byte) 145;
	public static final byte OPC_i2c = (byte) 146;
	public static final byte OPC_i2s = (byte) 147;
	public static final byte OPC_lcmp = (byte) 148;
	public static final byte OPC_fcmpl = (byte) 149;
	public static final byte OPC_fcmpg = (byte) 150;
	public static final byte OPC_dcmpl = (byte) 151;
	public static final byte OPC_dcmpg = (byte) 152;
	public static final byte OPC_ifeq = (byte) 153;
	public static final byte OPC_ifne = (byte) 154;
	public static final byte OPC_iflt = (byte) 155;
	public static final byte OPC_ifge = (byte) 156;
	public static final byte OPC_ifgt = (byte) 157;
	public static final byte OPC_ifle = (byte) 158;
	public static final byte OPC_if_icmpeq = (byte) 159;
	public static final byte OPC_if_icmpne = (byte) 160;
	public static final byte OPC_if_icmplt = (byte) 161;
	public static final byte OPC_if_icmpge = (byte) 162;
	public static final byte OPC_if_icmpgt = (byte) 163;
	public static final byte OPC_if_icmple = (byte) 164;
	public static final byte OPC_if_acmpeq = (byte) 165;
	public static final byte OPC_if_acmpne = (byte) 166;
	public static final byte OPC_goto = (byte) 167;
	public static final byte OPC_jsr = (byte) 168;
	public static final byte OPC_ret = (byte) 169;
	public static final byte OPC_tableswitch = (byte) 170;
	public static final byte OPC_lookupswitch = (byte) 171;
	public static final byte OPC_ireturn = (byte) 172;
	public static final byte OPC_lreturn = (byte) 173;
	public static final byte OPC_freturn = (byte) 174;
	public static final byte OPC_dreturn = (byte) 175;
	public static final byte OPC_areturn = (byte) 176;
	public static final byte OPC_return = (byte) 177;
	public static final byte OPC_getstatic = (byte) 178;
	public static final byte OPC_putstatic = (byte) 179;
	public static final byte OPC_getfield = (byte) 180;
	public static final byte OPC_putfield = (byte) 181;
	public static final byte OPC_invokevirtual = (byte) 182;
	public static final byte OPC_invokespecial = (byte) 183;
	public static final byte OPC_invokestatic = (byte) 184;
	public static final byte OPC_invokeinterface = (byte) 185;
	public static final byte OPC_new = (byte) 187;
	public static final byte OPC_newarray = (byte) 188;
	public static final byte OPC_anewarray = (byte) 189;
	public static final byte OPC_arraylength = (byte) 190;
	public static final byte OPC_athrow = (byte) 191;
	public static final byte OPC_checkcast = (byte) 192;
	public static final byte OPC_instanceof = (byte) 193;
	public static final byte OPC_monitorenter = (byte) 194;
	public static final byte OPC_monitorexit = (byte) 195;
	public static final byte OPC_wide = (byte) 196;
	public static final byte OPC_multianewarray = (byte) 197;
	public static final byte OPC_ifnull = (byte) 198;
	public static final byte OPC_ifnonnull = (byte) 199;
	public static final byte OPC_goto_w = (byte) 200;
	public static final byte OPC_jsr_w = (byte) 201;
}
