function [p2] = apply_homography(p1, H)
p1 = [p1(:);1];
t = H * p1;
p2 = [t(1)/t(3) , t(2)/t(3)];