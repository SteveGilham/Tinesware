count = 0
line = ""
ARGV[0].each_byte do |c| 
  line = line + ", " unless (count%2) > 0  or count == 0
  line = line + "0x" unless (count%2) > 0
  line << c unless c == 32
  count = count + 1 unless c == 32
  if 8 == count
    puts "            "+line+","
    count = 0
    line = ""
  end
end
puts line