package com.example.coin_panion.classes.utility;

import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;

public class Hashing {
    public static String keccakHash(String originalString){
        Keccak.Digest256 digest256 = new Keccak.Digest256();
        byte[] hashbytes = digest256.digest(
                originalString.getBytes(StandardCharsets.UTF_8));
        String sha3Hex = new String(Hex.encode(hashbytes));
        return sha3Hex;
    }
}
