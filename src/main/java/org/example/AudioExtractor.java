package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AudioExtractor {

    public static void main(String[] args) {
        // Path to the input video file
        String inputVideo = "video.mp4";

        // Get the audio codec from the video file
        String audioCodec = getAudioCodec(inputVideo);
        String outputAudio = determineOutputFormat(audioCodec,"audio");

        // Command to extract audio
        String command = String.format("ffmpeg -i %s -vn -acodec copy %s", inputVideo, outputAudio);

        // Execute the command
        executeCommand(command);
    }

    private static String getAudioCodec(String inputVideo) {
        String codec = "";
        String command = String.format("ffmpeg -i %s", inputVideo);

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Read the output to find the audio codec
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Check for audio stream information in the output
                    if (line.contains("Audio:")) {
                        // Extract the codec information
                        codec = line.split(",")[0].split("Audio: ")[1].trim();
                        break;
                    }
                }
            }
            // Wait for the process to finish
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return codec;
    }

    private static String determineOutputFormat(String audioCodec, String baseFilename) {
        // Default to aac if codec is not recognized
        String outputFormat = baseFilename;

        if (audioCodec.contains("aac")) {
            outputFormat += ".aac";
        } else if (audioCodec.contains("mp3")) {
            outputFormat += ".mp3";
        } else if (audioCodec.contains("vorbis")) {
            outputFormat += ".ogg";
        } else if (audioCodec.contains("flac")) {
            outputFormat += ".flac";
        } else {
            // Handle unrecognized audio codec
            System.out.println("Unrecognized audio codec. Defaulting to AAC.");
            outputFormat += ".aac";
        }

        return outputFormat;
    }

    private static void executeCommand(String command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Read the output of the command
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            // Wait for the process to finish
            int exitCode = process.waitFor();
            System.out.println("FFmpeg command executed with exit code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
