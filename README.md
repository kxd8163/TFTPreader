Simple TFTP client
=========================

TFTPreader is a console program that is designed to receive files via TFTP.

Usage: [java] TFTPreader [netascii|octet] tftp-host file [netascii|octet]

 - TFTP supports two transfer modes: netascii and octet. In a netascii transfer, the
data consists of lines of ASCII text followed by a carriage return and a linefeed
(CR/LF). An octet transfer treats the data as 8-bit bytes with no
interpretation.

 - tftp-host
Name of TFTP server the file that needs to transferred is located at.

 - file
name of the file, that needs to be transferred.

