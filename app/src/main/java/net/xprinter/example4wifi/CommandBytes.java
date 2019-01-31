package net.xprinter.example4wifi;

import java.util.ArrayList;
import java.util.List;
import java.io.UnsupportedEncodingException;

public class CommandBytes {
    //指令的紀錄，採byte格式
    List<Byte> commandList;

    public CommandBytes(){
        commandList = new ArrayList<Byte>();
    }


    private void addBytes(byte argInByte) {
        commandList.add(argInByte);
    }
    private void addBytes(byte[] argInBytes){
        byte[] inBytes = argInBytes;
        for (byte singleByte : inBytes) {
            commandList.add(singleByte);
        }
    }

    /**
     * 回傳列印指令，為byte[]格式
     * @return byte[]
     */
    public byte[] getBytes(){
        //將ArrayList轉Array
        Byte[] commands = commandList.toArray(new Byte[commandList.size()]);

        //轉換Byte[]至byte[]
        byte[] retCommands = new byte[commands.length];
        int retNum = 0;
        for (Byte command: commands) {
            retCommands[retNum++] = command.byteValue();
        }

        return retCommands;
    }

    /**
     * 轉換字串為byte陣列
     * @param text 列印文字
     * @return byte[]
     * @throws Exception
     */
    public byte[] StringtoBytes(String text) throws Exception{
        try {
            return text.getBytes("GBK");
        }catch(UnsupportedEncodingException e){
            //轉換格式錯誤
            throw new Exception("1");
        }
    }

    /**
     * 單據的表頭資料
     * @throws Exception
     */
    public void addTitle() throws Exception{
        addBytes(PrinterCommand.ESC_ALIGN_CENTER);
        addBytes(PrinterCommand.ESC_SELECT_BOLD);
        addBytes(StringtoBytes("國群網通"));
        addBytes(PrinterCommand.ESC_CANCEL_BOLD);
        addBytes(PrinterCommand.LF);
        addBytes(StringtoBytes("------------------------------------------------"));
        addBytes(PrinterCommand.LF);
        addBytes(PrinterCommand.ESC_ALIGN_LEFT);
        addBytes(StringtoBytes("日期：2019-01-29"));
        addBytes(PrinterCommand.LF);
        addBytes(PrinterCommand.ESC_ALIGN_RIGHT);
        addBytes(StringtoBytes("訂單編號：20190129001"));
        addBytes(PrinterCommand.LF);
        addBytes(PrinterCommand.ESC_ALIGN_LEFT);
        addBytes(StringtoBytes("統編：-"));
        addBytes(PrinterCommand.LF);
        addBytes(StringtoBytes("收銀員：admin"));
        addBytes(PrinterCommand.LF);
        addBytes(StringtoBytes("------------------------------------------------"));
        addBytes(PrinterCommand.LF);
    }

    /**
     * 單據的表身資料
     * @throws Exception
     */
    public void addBody() throws Exception{
        //-----------------表頭---------------------
        addBytes(StringtoBytes("品項"));
        addBytes(PrinterCommand.ABSOLUTION_POS_A);
        addBytes(StringtoBytes("單價\t"));
        addBytes(PrinterCommand.ABSOLUTION_POS_B);
        addBytes(StringtoBytes("數量\t"));
        addBytes(StringtoBytes("小計"));
        addBytes(PrinterCommand.LF);
        //-----------------資料--------------------

        addBytes(StringtoBytes("測試料品A"));
        addBytes(PrinterCommand.ABSOLUTION_POS_A);
        addBytes(StringtoBytes("50.0\t"));
        addBytes(PrinterCommand.ABSOLUTION_POS_B);
        addBytes(StringtoBytes("5\t"));
        addBytes(StringtoBytes("250.0"));
        addBytes(PrinterCommand.LF);

        addBytes(StringtoBytes("測試料品B"));
        addBytes(PrinterCommand.ABSOLUTION_POS_A);
        addBytes(StringtoBytes("500.0\t"));
        addBytes(PrinterCommand.ABSOLUTION_POS_B);
        addBytes(StringtoBytes("15\t"));
        addBytes(StringtoBytes("7500.0"));
        addBytes(PrinterCommand.LF);

        addBytes(StringtoBytes("測試料品CCCC"));
        addBytes(PrinterCommand.ABSOLUTION_POS_A);
        addBytes(StringtoBytes("100.0\t"));
        addBytes(PrinterCommand.ABSOLUTION_POS_B);
        addBytes(StringtoBytes("1\t"));
        addBytes(StringtoBytes("100.0"));
        addBytes(PrinterCommand.LF);
        addBytes(StringtoBytes("------------------------------------------------"));

        //-----------------頁尾--------------------

        addBytes(PrinterCommand.ESC_ALIGN_LEFT);
        addBytes(StringtoBytes("未稅金額： 7850"));
        addBytes(PrinterCommand.LF);
        addBytes(StringtoBytes("營業稅： 393"));
        addBytes(PrinterCommand.LF);
        addBytes(StringtoBytes("總金額： 8243"));
        addBytes(PrinterCommand.LF);

        //-----------------切紙--------------------
        addBytes(PrinterCommand.FEED_PAPER_AND_CUT);
    }
}
