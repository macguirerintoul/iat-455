
import java.io.ByteArrayOutputStream;

class Assignment1 {
    public Assignment1() {
        question3();
    }

    private void question3() {
        System.out.println("Question 3");
        byte[] input = "1111111111110001111111111100000111111111111111111111100111111111111".getBytes();
        System.out.println("Original length:");
        System.out.println(input.length);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte lastByte = input[0];
        int matchCount = 1;
        for (int i = 1; i < input.length; i++) {
            byte thisByte = input[i];
            if (lastByte == thisByte) {
                matchCount++;
            } else {
                output.write((byte) matchCount);
                output.write((byte) lastByte);
                matchCount = 1;
                lastByte = thisByte;
            }
        }
        output.write((byte) matchCount);
        output.write((byte) lastByte);

        System.out.println("Compressed length:");
        System.out.println(output.toByteArray().length);

        System.out.println("Reduction in size:");
        System.out.println(100 - ((double) output.toByteArray().length / input.length) * 100 + "%");
    }

    public static void main(String[] args) {
        Assignment1 a1 = new Assignment1();
        System.out.println("Complete");
	}
}