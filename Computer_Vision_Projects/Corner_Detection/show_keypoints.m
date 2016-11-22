
for i = 1:1:10
    im = imread(strcat('images/image',int2str(i),'.jpg'));
    scale = 400 / size(im,1);
    im = imresize(im,scale);
    
    [x, y, scores, Ix, Iy] = extract_keypoints(im);
    
    figure; imshow(im); hold on;
        
    for j = 1:1:size(x,2)
        plot( x(j), y(j), 'ro', 'MarkerSize', 2);
    end
    hold off;
    
    saveas(gcf,strcat('Homework 4/out_images/out_image',int2str(i),'.jpg'));
    [feat, x, y, scores ] = compute_features( x, y, scores, Ix, Iy );
end



